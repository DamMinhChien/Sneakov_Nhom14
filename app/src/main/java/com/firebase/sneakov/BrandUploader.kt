package com.firebase.sneakov

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import java.nio.charset.Charset

object BrandUploader {

    private const val TAG = "BrandUploader"

    fun uploadBrands(context: Context) {
        val db = FirebaseFirestore.getInstance()

        val jsonString = try {
            context.assets.open("brands.json")
                .readBytes()
                .toString(Charset.defaultCharset())
        } catch (e: Exception) {
            Log.e(TAG, "Error reading JSON file", e)
            return
        }

        val brandsArray = try {
            JSONArray(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Invalid JSON format", e)
            return
        }

        for (i in 0 until brandsArray.length()) {
            val brandJson = brandsArray.getJSONObject(i)
            val brandMap = mutableMapOf<String, Any>()

            brandJson.keys().forEach { key ->
                val value = brandJson.get(key)
                brandMap[key] = if (key == "created_at") FieldValue.serverTimestamp() else value
            }

            val brandId = brandMap["id"]?.toString() ?: db.collection("brands").document().id
            val brandRef = db.collection("brands").document(brandId)

            brandRef.set(brandMap)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Added brand ${brandMap["name"]} with ID $brandId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding brand ${brandMap["name"]}", e)
                }
        }
    }
}
