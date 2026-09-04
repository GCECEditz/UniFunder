package com.example

import android.util.Log
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun getResponse(chatHistory: List<ChatMessage>, prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) { "" }

        if (apiKey.isEmpty()) {
            Log.w(TAG, "Gemini API key is not set. Using simulated response.")
            return@withContext getSimulatedResponse(prompt)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

        val contentsArray = JSONArray().apply {
            //loop and add all previous messages stored in our history
            for (message in chatHistory) {
                put(JSONObject().apply {
                    val role = if (message.isUser) "user" else "model"
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", message.text) })
                    })
                })
            }
            //user query message
            put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", prompt) })
                })
            })
        }

        val jsonPayload = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(
                        JSONObject().apply {
                            put("text", "You are UniFunder AI, a smart assistant helping university students and staff in Malaysia partner with NGOs and build successful fundraising campaigns. Keep your answers clear, actionable, friendly, and structured. Incorporate SDG 17 (Partnerships for the Goals) where relevant. You do not have to reintroduce yourself after your first response")
                        }
                    )
                })
            })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("x-goog-api-key", apiKey)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API Error: status code ${response.code}, body: $errBody")
                    return@withContext "API Error: ${response.code}. Falling back to offline assistant.\n\n${getSimulatedResponse(prompt)}"
                }
                
                val responseBody = response.body?.string() ?: return@withContext "Error: Empty response body"
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val firstPart = parts?.optJSONObject(0)
                val text = firstPart?.optString("text")

                if (!text.isNullOrEmpty()) {
                    return@withContext text
                } else {
                    return@withContext "I received an empty response from Gemini, but here is how I can assist:\n\n${getSimulatedResponse(prompt)}"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception during Gemini API call", e)
            return@withContext "Connection error. Using offline assistant.\n\n${getSimulatedResponse(prompt)}"
        }
    }

    private fun getSimulatedResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("proposal") || lower.contains("optimize") -> {
                "Here are 3 ways to optimize your project proposal for Mount Miriam Cancer Hospital:\n\n" +
                "1. **Quantify the Impact**: Clearly state how the $500 fundraising goal will directly translate to patient care (e.g., funding patient diagnostic tests).\n" +
                "2. **University Collaboration**: Emphasize how TARUMT students will organize a charity food drive, promoting student-NGO alignment (SDG 17).\n" +
                "3. **Incentive Structure**: Propose offering student participation certificates and co-curricular points to boost engagement."
            }
            lower.contains("budget") || lower.contains("fund") -> {
                "For your Community Garden Project budget, I recommend allocating:\n\n" +
                "- **40% Procurement**: Purchasing high-quality soil, organic seeds, and basic gardening tools.\n" +
                "- **30% Promotion**: Designing eye-catching flyers and social media ads to attract donors.\n" +
                "- **20% Logistics**: Transportation and university venue permissions.\n" +
                "- **10% Contingency**: Emergency reserve funds."
            }
            lower.contains("hello") || lower.contains("hi ") || lower.contains("hey") -> {
                "Hello! I am your UniFunder NGO Partner Assistant. How can I help you today with your project proposals, budgeting plans, or social media fundraising campaigns?"
            }
            else -> {
                "That is a great question! For a university-NGO partnership, remember to coordinate with your target NGO early. Aligning your student volunteer effort with their specific needs (e.g., Cancer Care or Habitat Protection) under SDG 17 will maximize your chances of getting your proposal approved and hitting your funding goals!"
            }
        }
    }
}
