package com.example.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Request data classes for Moshi serialization
    data class Content(val parts: List<Part>)
    data class Part(val text: String)
    data class GenerateContentRequest(val contents: List<Content>, val systemInstruction: Content? = null)

    // Response data classes for Moshi deserialization
    data class ResponsePart(val text: String?)
    data class ResponseContent(val parts: List<ResponsePart>?)
    data class ResponseCandidate(val content: ResponseContent?)
    data class GenerateContentResponse(val candidates: List<ResponseCandidate>?)

    private val requestAdapter = moshi.adapter(GenerateContentRequest::class.java)
    private val responseAdapter = moshi.adapter(GenerateContentResponse::class.java)

    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey == "MY_GEMINI_API_KEY" || apiKey.isEmpty()) {
            return@withContext "Kunci API Gemini (GEMINI_API_KEY) belum terpasang di Panel Rahasia AI Studio Anda. Silakan pasang terlebih dahulu untuk mengaktifkan Konsultan AI."
        }

        val model = "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val contents = listOf(Content(parts = listOf(Part(text = prompt))))
        val sysInstruction = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
        val requestObj = GenerateContentRequest(contents = contents, systemInstruction = sysInstruction)
        val jsonRequest = requestAdapter.toJson(requestObj)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonRequest.toRequestBody(mediaType)

        val httpRequest = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(httpRequest).execute().use { response ->
                if (!response.isSuccessful) {
                    val errString = response.body?.string() ?: ""
                    Log.e(TAG, "Unsuccessful response: $response, body: $errString")
                    return@withContext "Gagal terhubung dengan layanan Gemini (HTTP ${response.code})."
                }

                val jsonResponse = response.body?.string()
                Log.d(TAG, "Response JSON: $jsonResponse")
                if (jsonResponse != null) {
                    val parsedResponse = responseAdapter.fromJson(jsonResponse)
                    val textResult = parsedResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    return@withContext textResult ?: "Gemini mengembalikan respon kosong."
                } else {
                    return@withContext "Respon kosong dari server."
                }
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "No internet access", e)
            return@withContext "Tidak ada koneksi internet. Pastikan perangkat Anda terhubung ke internet."
        } catch (e: Exception) {
            Log.e(TAG, "Request error", e)
            return@withContext "Error koneksi: ${e.localizedMessage ?: e.message}"
        }
    }
}
