package com.firebase.sneakov.data.repository

import android.util.Log
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.model.ProductVariant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class CartRepository(
    private val firestore: FirebaseFirestore
) {

    private val cartCollection = firestore.collection("carts")
    private val productCollection = firestore.collection("products")

    /**
     *  Lấy danh sách giỏ hàng của user.
     */
    suspend fun getUserCart(userId: String): List<Pair<Cart, Product?>> = try {
        val cart = cartCollection.whereEqualTo("userId", userId)
            .get()
            .await()
        // danh sach giỏ hàng
        val cartList = cart.documents.mapNotNull { doc ->
            doc.toObject(Cart::class.java)?.copy(id = doc.id)
        }

        //truy vấn sang products để thông tin
        val res = mutableListOf<Pair<Cart, Product?>>()
        for (item in cartList) {
            val productDoc = productCollection.document(item.productId).get().await()
            val product = if (productDoc.exists()) {
                val baseProduct = productDoc.toObject(Product::class.java)?.copy(id = productDoc.id)

                val variantsSnapshot = productCollection
                    .document(item.productId)
                    .collection("variants")
                    .get()
                    .await()
                val variants = variantsSnapshot.documents.mapNotNull { variantDoc ->
                    variantDoc.toObject(ProductVariant::class.java)
                        ?.copy(id = variantDoc.id)
                }

                baseProduct?.copy(variants = variants)
            } else null
            res.add(item to product)
        }
        res
    }catch (e: Exception) {
        emptyList()
    }

    suspend fun addToCart(userId: String, productId: String, variantId: String, quantity: Int): Boolean {
        try {
            //lấy product và variants của nó
            val productDoc = productCollection.document(productId)
                .get()
                .await()

            if(!productDoc.exists()) return false

            val baseProduct = productDoc.toObject(Product::class.java) ?: return false
            val variantDoc = productCollection.document(productId)
                .collection("variants")
                .document(variantId)
                .get()
                .await()
            val variant = variantDoc.toObject(ProductVariant::class.java) ?: return false
            Log.d("CartRepo", "Variant: ${variant.stock}")
            // lấy item trong cart hiện tại
            val existingCartSnapshot = cartCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("productId", productId)
                .whereEqualTo("variantId", variantId)
                .get()
                .await()
            val existingCartItem = existingCartSnapshot.documents.firstOrNull()
            val currentQuantity = existingCartItem?.getLong("quantity")?.toInt() ?: 0
            val totalQuantity = currentQuantity + quantity

            //kiểm tra tồn kho
            if (totalQuantity > variant.stock) {
                return false
            }

            if(existingCartItem != null) {
                cartCollection.document(existingCartItem.id)
                    .update("quantity", totalQuantity)
                    .await()
            }else {
                val newItem = hashMapOf(
                    "userId" to userId,
                    "productId" to productId,
                    "variantId" to variantId,
                    "quantity" to quantity,
                    "added_at" to com.google.firebase.Timestamp.now()
                )
                cartCollection.add(newItem).await()
            }
            return  true
        }catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun updateQuantity(cartId: String, newQuantity: Int): Boolean {
       return try {
           val cartDoc = cartCollection.document(cartId).get().await()
           if(!cartDoc.exists()) return false

           val productId = cartDoc.getString("productId") ?: return false
           val variantId = cartDoc.getString("variantId") ?: return false

           val variantDoc = productCollection.document(productId)
               .collection("variants")
               .document(variantId)
               .get()
               .await()

           val variant = variantDoc.toObject(ProductVariant::class.java) ?: return false

           if(newQuantity > variant.stock) return false

           cartCollection.document(cartId)
               .update("quantity", newQuantity)
               .await()
           true
       }catch (e: Exception) {
           e.printStackTrace()
           false
       }

    }

    suspend fun removeFromCart(cartId: String) {
        cartCollection.document(cartId).delete().await()
    }

}