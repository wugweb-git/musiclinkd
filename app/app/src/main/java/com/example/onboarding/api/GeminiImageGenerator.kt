package com.example.onboarding.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object GeminiImageGenerator {
    private const val TAG = "GeminiImageGenerator"
    private const val BASE_URL = "https://generativelanguage.googleapis.com"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Calls Gemini API to generate an image based on prompt, model, and aspect ratio.
     * Returns a pair of (Bitmap, base64String) on success, or null on failure.
     */
    suspend fun generateImage(
        prompt: String,
        modelName: String,
        aspectRatio: String
    ): Pair<Bitmap, String>? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is not configured correctly.")
            return@withContext null
        }

        val url = "$BASE_URL/v1beta/models/$modelName:generateContent?key=$apiKey"

        // Build the request body with org.json
        val jsonRequest = JSONObject()

        // contents
        val contentObj = JSONObject()
        val partsArray = org.json.JSONArray()
        val partObj = JSONObject()
        partObj.put("text", prompt)
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)

        val contentsArray = org.json.JSONArray()
        contentsArray.put(contentObj)
        jsonRequest.put("contents", contentsArray)

        // generationConfig
        val generationConfig = JSONObject()
        val imageConfig = JSONObject()
        imageConfig.put("aspectRatio", aspectRatio)
        imageConfig.put("imageSize", "1K") // default size config for Imagen models
        generationConfig.put("imageConfig", imageConfig)

        val responseModalities = org.json.JSONArray()
        responseModalities.put("TEXT")
        responseModalities.put("IMAGE")
        generationConfig.put("responseModalities", responseModalities)

        jsonRequest.put("generationConfig", generationConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonRequest.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed with code: ${response.code}, body: $bodyString")
                    return@withContext null
                }

                val jsonResponse = JSONObject(bodyString)
                val candidates = jsonResponse.optJSONArray("candidates") ?: return@withContext null
                if (candidates.length() == 0) {
                    Log.e(TAG, "Empty candidates array in response: $bodyString")
                    return@withContext null
                }

                val content = candidates.getJSONObject(0).optJSONObject("content") ?: return@withContext null
                val parts = content.optJSONArray("parts") ?: return@withContext null

                var base64Data: String? = null
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    if (part.has("inlineData")) {
                        val inlineData = part.getJSONObject("inlineData")
                        base64Data = inlineData.optString("data")
                        break
                    }
                }

                if (base64Data != null && base64Data.isNotEmpty()) {
                    val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    if (bitmap != null) {
                        return@withContext Pair(bitmap, base64Data)
                    }
                } else {
                    Log.e(TAG, "Could not find inlineData/data in response parts: $bodyString")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating image", e)
        }
        return@withContext null
    }

    /**
     * Saves a Bitmap as a local file inside the app cache/files directory.
     * Returns the absolute file path of the saved file, or null on failure.
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap, prefix: String): String? {
        return try {
            val file = File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap to cache file", e)
            null
        }
    }
}
