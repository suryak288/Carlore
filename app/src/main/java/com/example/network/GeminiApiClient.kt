package com.example.network

import com.example.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Part(val text: String? = null)
data class Content(val parts: List<Part>)
data class GenerationConfig(val responseMimeType: String? = null, val temperature: Float? = null)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)
data class Candidate(val content: Content)
data class GenerateContentResponse(val candidates: List<Candidate>? = null)

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun fetchVehicleReport(regNumber: String, apiKey: String): VehicleReport? = withContext(Dispatchers.IO) {
        val queryReg = regNumber.uppercase().trim()
        val prompt = """
            You are CarLore's vehicle intelligence engine. Given an Indian vehicle registration number "$queryReg", generate a realistic mock vehicle report as JSON.
            Return ONLY valid JSON, no explanation, no markdown.
            
            JSON structure:
            {
              "carName": "string",
              "regNumber": "string",
              "year": 2018,
              "variant": "string",
              "fuel": "string",
              "odometer": 50000,
              "trustScore": 75,
              "owners": 1,
              "city": "string",
              "checks": [
                { "label": "string", "status": "pass"|"warn"|"fail", "detail": "string" }
              ],
              "flags": ["string"],
              "ownershipHistory": [
                { "ownerNum": 1, "city": "string", "fromYear": 2018, "toYear": 2024, "km": 50000 }
              ],
              "scoreBreakdown": {
                "history": 70, "condition": 65, "documents": 50, "mileage": 80
              },
              "upcomingRepairs": [
                { "part": "string", "urgency": "soon"|"watch"|"ok", "estimateLow": 5000, "estimateHigh": 10000 }
              ]
            }
        """.trimIndent()

        try {
            val req = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.5f)
            )
            val response = apiService.generateContent(apiKey, req)
            val jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return@withContext null
            
            val cleanJson = jsonString.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            moshi.adapter(VehicleReport::class.java).fromJson(cleanJson)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
