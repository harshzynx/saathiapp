package harshzynx.saathi.domain

import android.util.Log
import harshzynx.saathi.data.api.GroqApiService
import harshzynx.saathi.data.api.GroqChatRequest
import harshzynx.saathi.data.api.GroqMessage
import harshzynx.saathi.data.local.ApiKeyManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqService @Inject constructor(
    private val groqApiService: GroqApiService,
    private val okHttpClient: OkHttpClient,
    private val apiKeyManager: ApiKeyManager
) {
    private val TAG = "GroqService"

    private val baseSystemInstruction =
        """
        You are Saathi, an intelligent, warm, highly conversational AI companion for Indian students and youth.
        Talk in natural Hinglish/Hindi or English as the user speaks.
        Be super supportive, friendly, engaging, and conversational.
        Ask thoughtful follow-up questions to keep the conversation flowing naturally like a real friend.
        Never repeat static boilerplate phrases. Speak like a close caring friend!
        Do not claim to be a doctor or therapist.
        """.trimIndent()

    private val supportedModels = listOf(
        "llama-3.3-70b-versatile",
        "llama3-70b-8192",
        "llama3-8b-8192"
    )

    suspend fun getChatResponse(
        userMessage: String,
        conversationHistory: List<GroqMessage> = emptyList(),
        isJustListen: Boolean = false,
        category: String? = null
    ): String = withContext(Dispatchers.IO) {

        val cleanedMessage = userMessage.trim()

        require(cleanedMessage.isNotBlank()) {
            "Message cannot be empty."
        }

        val lower = cleanedMessage.lowercase()
        val urgentTerms = listOf(
            "suicide", "kill myself", "end my life", "want to die",
            "marne ka mann", "mar jana chahta", "mar jana chahti",
            "jaan dena", "khudkushi", "khatam karna hai"
        )

        if (urgentTerms.any { lower.contains(it) }) {
            return@withContext """
                Mujhe afsos hai ki tum itna mushkil waqt face kar rahe ho.
                Please abhi akele mat raho—kisi trusted person ko call karo ya unke paas jao.
                India mein Tele-MANAS se 14416 par support le sakte ho.
                Agar turant khatra hai, local emergency services se contact karo. Kya tum is waqt safe jagah par ho?
            """.trimIndent()
        }

        val systemPrompt = buildString {
            append(baseSystemInstruction)
            if (isJustListen) {
                append("\nThe user prefers listening and emotional validation. Listen first and avoid unsolicited advice.")
            }
            if (!category.isNullOrBlank()) {
                append("\nConversation topic: ${category.take(100)}")
            }
        }

        val userKey = apiKeyManager.getApiKey().trim()

        // 1. If user provided a custom Groq API Key in Settings, try live Groq Cloud API
        if (userKey.isNotBlank()) {
            val authorizationHeader = if (userKey.startsWith("Bearer ", ignoreCase = true)) userKey else "Bearer $userKey"

            val messagesList = mutableListOf<GroqMessage>()
            messagesList.add(GroqMessage(role = "system", content = systemPrompt))
            conversationHistory.takeLast(10).forEach { msg ->
                if (msg.content.isNotBlank()) {
                    messagesList.add(msg)
                }
            }
            messagesList.add(GroqMessage(role = "user", content = cleanedMessage))

            for (modelName in supportedModels) {
                try {
                    Log.d(TAG, "Sending Chat Completion request to Groq API using model: $modelName")

                    val request = GroqChatRequest(
                        messages = messagesList,
                        model = modelName,
                        temperature = 0.7,
                        maxTokens = 800
                    )

                    val response = groqApiService.getChatCompletion(authorizationHeader, request)

                    Log.d(TAG, "Groq API response status code: ${response.code()} for model: $modelName")

                    if (response.isSuccessful) {
                        val body = response.body()
                        val content = body?.choices?.firstOrNull()?.message?.content?.trim()

                        if (!content.isNullOrBlank()) {
                            Log.d(TAG, "Groq AI response received successfully using model: $modelName")
                            return@withContext content
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during Groq request for model $modelName: ${e.localizedMessage}")
                }
            }
        }

        // 2. High-speed Public Live Online AI Engine via Pollinations POST API
        try {
            val messages = JSONArray()
                .put(JSONObject().put("role", "system").put("content", systemPrompt))
                .put(JSONObject().put("role", "user").put("content", cleanedMessage))

            val jsonBody = JSONObject()
                .put("messages", messages)
                .put("model", "openai")
                .put("json", false)

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val postRequest = Request.Builder()
                .url("https://text.pollinations.ai/")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile)")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()

            okHttpClient.newCall(postRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val reply = response.body?.string()?.trim().orEmpty()
                    if (reply.isNotBlank() && !reply.startsWith("{") && !reply.contains("\"error\"")) {
                        return@withContext reply
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Try GET fallback
        }

        // 3. High-speed Public Live Online AI Engine via Pollinations GET API
        try {
            val encodedPrompt = URLEncoder.encode(cleanedMessage, "UTF-8")
            val encodedSystem = URLEncoder.encode(systemPrompt, "UTF-8")
            val getUrl = "https://text.pollinations.ai/$encodedPrompt?system=$encodedSystem&model=openai"

            val getRequest = Request.Builder()
                .url(getUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile)")
                .get()
                .build()

            okHttpClient.newCall(getRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val reply = response.body?.string()?.trim().orEmpty()
                    if (reply.isNotBlank() && !reply.startsWith("{") && !reply.contains("\"error\"")) {
                        return@withContext reply
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Fallthrough to dynamic local engine if 100% offline
        }

        // 4. Fallback: Advanced Dynamic Offline Response Generator (if 100% offline)
        return@withContext generateLocalSaathiResponse(cleanedMessage, isJustListen, category)
    }

    fun generateLocalSaathiResponse(
        message: String,
        isJustListen: Boolean = false,
        category: String? = null
    ): String {
        val lower = message.lowercase().trim()

        if (isJustListen) {
            val listenReplies = listOf(
                "Main samajh raha hoon dost 💙. Main yahan hoon bas tumhari baat bina kisi judgement ke sunne ke liye. Aage kaho, dil halka kar lo.",
                "Tumhaari feelings bilkul valid hain 🫂. Zaroori nahi ki har cheez ka solution abhi nikalein. Main yahan hoon, bolo jo bolna chahte ho.",
                "Main poori tarah se sun raha hoon. Chaho toh thoda aur batao, kaisa feel ho raha hai abhi?"
            )
            return listenReplies.random()
        }

        // 1. Greetings
        val greetings = listOf("hi", "hello", "hey", "namaste", "kaise ho", "kya haal", "hii", "heyy")
        if (greetings.any { lower.contains(it) }) {
            val greetResponses = listOf(
                "Namaste my friend! 🌱 Main Saathi hoon. Aaj tumhara din kaisa chal raha hai?",
                "Hey! Kaisa feel kar rahe ho aaj? Main hamesha tumhari baat sunne ke liye ready hoon ✨.",
                "Namaste dost! Aaj man mein kya chal raha hai? Kuch share karna chahoge?"
            )
            return greetResponses.random()
        }

        // 2. Exam & Academics
        val studyKeywords = listOf("exam", "study", "marks", "padhai", "paper", "college", "school", "test", "syllabus", "backlog")
        if (studyKeywords.any { lower.contains(it) }) {
            val studyResponses = listOf(
                "Padhai aur exams ka tension kaafi heavy ho sakta hai 📚. Lekin yaad rakho: Tumhaari worth sirf tumhaare marks se define nahi hoti. Chote 25-minute study blocks me padho, it helps!",
                "Exam tension me zyaada load mat lo 🌬️. Ek baar me ek hi topic par focus karo. Tumne pehle bhi mehnat ki hai, is baar bhi accha karoge!",
                "Padhai me fatigue aana normal hai. Ek cup chai/paani piyo, 5 min stretch karo, fir fresh mind se shuru karte hain ☕."
            )
            return studyResponses.random()
        }

        // 3. Stress, Anxiety & Depression
        val stressKeywords = listOf("stress", "tension", "anxiety", "darr", "fear", "overthink", "soch", "depression", "depressed", "low", "heavy")
        if (stressKeywords.any { lower.contains(it) }) {
            val stressResponses = listOf(
                "Tension aur heavy feelings dimag ko thaka deti hain 🌀. Ek lambi deep breath lo... Inhale 1..2..3.. Exhale... Present moment me aao, sab control me ho jayega.",
                "Main samajh sakta hoon ki tum kaisa feel kar rahe ho 🌿. Kuch waqt ke liye thoda relax karo aur paani piyo. Hum milkar isse handle kar lenge!",
                "Sab kuch ek hi din me solve hona zaroori nahi hai ✨. Step-by-step aage badhte hain. Abhi tum sabse zyaada kya feel kar rahe ho?"
            )
            return stressResponses.random()
        }

        // 4. Relationships & Friends
        val relKeywords = listOf("friend", "breakup", "love", "relationship", "dost", "fight", "crush")
        if (relKeywords.any { lower.contains(it) }) {
            val relResponses = listOf(
                "Relationships aur dosti me ups & downs aana natural hai 💔. Kabhi-kabhi thoda distance aur patience cheezon ko clear kar deta hai. Tum kaisa feel kar rahe ho?",
                "KISI pyare insaan se misunderstanding hona dukhi karta hai 🤝. Thoda calm hokar sochne se raasta nikal aayega. Main tumhare saath hoon!"
            )
            return relResponses.random()
        }

        // 5. Hostel & Family
        val homeKeywords = listOf("hostel", "home", "ghar", "family", "mummy", "papa", "lonely", "akela")
        if (homeKeywords.any { lower.contains(it) }) {
            val homeResponses = listOf(
                "Ghar se door rehna ya family pressure handle karna aasan nahi hota 🏡. Aise waqt me akela feel hona normal hai. Saathi hamesha tumhare saath hai!",
                "Ghar walon ki baatein kabhi kabhi heavy lag sakti hain. Unki tension alag hoti hai, par tum apni health ka bhi dhyan rakho 🌿."
            )
            return homeResponses.random()
        }

        val dynamicFallbacks = listOf(
            "Main tumhari baat bohot dhyan se samajh raha hoon 🌱. Ye batao ki is baare mein tum aage kya soch rahe ho?",
            "Tumhara har emotion valid hai 💙. Jo tum keh rahe ho, usme kaafi depth hai. Thoda aur detail mein batana chahoge?",
            "Main hamesha tumhare saath hoon ✨. Aisi baatein share karke tumne bohot sahi kiya. Abhi tumhe sabse zyaada kis cheez ki zaroorat feel ho rahi hai?",
            "Samajh raha hoon! Life mein aisa waqt aata hai. Lekin yaad rakho tum akele nahi ho 🌿. Kya hum iska koi chota sa solution dhoondhein?",
            "Tumhaari baat sun kar achha laga. Tum bohot honest ho apne thoughts ke saath 💙. Aur kya chal raha mind mein?"
        )

        return dynamicFallbacks.random()
    }
}
