package com.firebase.sneakov

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import java.nio.charset.Charset

object FirebaseUploader {

    private const val TAG = "FirebaseUploader"

    fun uploadProducts(context: Context) {
        val db = FirebaseFirestore.getInstance()

        // 1. Load JSON từ assets
        val jsonString = try {
            context.assets.open("products.json")
                .readBytes()
                .toString(Charset.defaultCharset())
        } catch (e: Exception) {
            Log.e(TAG, "Error reading JSON file", e)
            return
        }

        val productsArray = try {
            JSONArray(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Invalid JSON format", e)
            return
        }

        // 2. Duyệt từng product
        for (i in 0 until productsArray.length()) {
            val productJson = productsArray.getJSONObject(i)

            // Tách variants ra riêng
            val variantsJsonArray = productJson.optJSONArray("variants")

            // 3. Build productMap, convert colors
            val productMap = mutableMapOf<String, Any>()
            productJson.keys().forEach { key ->
                if (key == "variants" || key == "id") return@forEach // ⛔ Bỏ id trong productMap

                val value = productJson.get(key)

                // Chuyển colors JSONArray → List<Map<String, Any>>
                if (key == "colors" && value is JSONArray) {
                    val colorsList = mutableListOf<Map<String, Any>>()
                    for (j in 0 until value.length()) {
                        val colorJson = value.getJSONObject(j)
                        val imagesJsonArray = colorJson.optJSONArray("images")
                        val imagesList = mutableListOf<String>()

                        if (imagesJsonArray != null) {
                            for (k in 0 until imagesJsonArray.length()) {
                                imagesList.add(imagesJsonArray.getString(k))
                            }
                        }

                        val colorMap = mapOf(
                            "name" to colorJson.getString("name"),
                            "hex" to colorJson.getString("hex"),
                            "images" to imagesList
                        )
                        colorsList.add(colorMap)
                    }
                    productMap["colors"] = colorsList

                } else if (key == "created_at") {
                    productMap["created_at"] = FieldValue.serverTimestamp()

                } else {
                    productMap[key] = value
                }
            }

            // 4. Lấy ID product → lấy từ JSON, nhưng không add vào map
            val productId = productJson.optString("id").ifEmpty {
                db.collection("products").document().id
            }

            val productRef = db.collection("products").document(productId)

            // 5. Upload product
            productRef.set(productMap)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Added product ${productMap["name"]} with ID $productId")

                    // 6. Upload variants vào subcollection với ID riêng
                    variantsJsonArray?.let { variants ->
                        for (j in 0 until variants.length()) {
                            val variantJson = variants.getJSONObject(j)
                            val variantId = variantJson.optString("id").ifEmpty {
                                "${productId}_${variantJson.getString("color")}_${variantJson.getInt("size")}"
                            }

                            val variantMap = mutableMapOf<String, Any>()
                            variantJson.keys().forEach { key ->
                                if (key != "id") { // bỏ id trong map
                                    variantMap[key] = variantJson.get(key)
                                }
                            }

                            productRef.collection("variants")
                                .document(variantId)
                                .set(variantMap)
                                .addOnSuccessListener {
                                    Log.d(TAG, "   ➕ Added variant $variantId")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error adding variant $variantId", e)
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding product ${productMap["name"]}", e)
                }
        }
    }
}
