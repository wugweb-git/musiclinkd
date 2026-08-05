package com.example.onboarding.ai

import com.example.BuildConfig
import com.example.onboarding.util.ErrorReportingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CollaboratorRecommendation(
    val name: String,
    val role: String,
    val location: String,
    val matchScore: Int,
    val projectIdea: String,
    val synergyReason: String,
    val genres: List<String>,
    val avatarEmoji: String = "🎵"
)

data class TrendingMusicProject(
    val id: String,
    val title: String,
    val creatorName: String,
    val creatorRole: String,
    val coverImageUrl: String,
    val genre: String,
    val matchScore: Int,
    val seekingRoles: List<String>,
    val description: String,
    val audioSnippetTitle: String,
    val listensCount: String,
    val isVerifiedCreator: Boolean = true
)

object GeminiRecommendationEngine {
    private const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun fetchCollaboratorRecommendations(
        userGenres: String,
        userSkills: String
    ): List<CollaboratorRecommendation> = withContext(Dispatchers.IO) {
        ErrorReportingService.logBreadcrumb("fetchCollaboratorRecommendations started with genres: $userGenres")
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ErrorReportingService.logWarning("GeminiEngine", "GEMINI_API_KEY build config read failed: ${e.message}")
            ""
        }

        if (apiKey.isNullOrEmpty() || apiKey == "null" || apiKey == "YOUR_GEMINI_API_KEY") {
            return@withContext getFallbackRecommendations(userGenres, userSkills)
        }

        val prompt = """
            Act as an AI Music Industry Matchmaker in India.
            A user has genre interests: "$userGenres" and skills: "$userSkills".
            Suggest 3 potential Indian musicians to collaborate with.
            Return ONLY a raw JSON array of 3 objects with exact fields:
            "name" (String), "role" (String), "location" (String), "matchScore" (Int 85-99),
            "projectIdea" (String), "synergyReason" (String), "genres" (Array of Strings), "avatarEmoji" (String emoji).
            No markdown formatting or extra text.
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("$GEMINI_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotEmpty()) {
                val root = JSONObject(responseString)
                val candidates = root.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    val cleanJson = text.replace("```json", "").replace("```", "").trim()
                    if (cleanJson.startsWith("[")) {
                        val jsonArray = JSONArray(cleanJson)
                        val resultList = mutableListOf<CollaboratorRecommendation>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val genreArray = obj.optJSONArray("genres")
                            val genreList = mutableListOf<String>()
                            if (genreArray != null) {
                                for (g in 0 until genreArray.length()) {
                                    genreList.add(genreArray.getString(g))
                                }
                            }
                            resultList.add(
                                CollaboratorRecommendation(
                                    name = obj.optString("name", "Musician"),
                                    role = obj.optString("role", "Producer"),
                                    location = obj.optString("location", "Mumbai"),
                                    matchScore = obj.optInt("matchScore", 92),
                                    projectIdea = obj.optString("projectIdea", "Collaboration Project"),
                                    synergyReason = obj.optString("synergyReason", "High musical synergy."),
                                    genres = if (genreList.isNotEmpty()) genreList else listOf("Indie", "Fusion"),
                                    avatarEmoji = obj.optString("avatarEmoji", "🎵")
                                )
                            )
                        }
                        if (resultList.isNotEmpty()) return@withContext resultList
                    }
                }
            }
        } catch (e: Exception) {
            ErrorReportingService.logException(e, "GeminiEngine", "Failed to fetch collaborator recommendations from Gemini API")
        }

        return@withContext getFallbackRecommendations(userGenres, userSkills)
    }

    suspend fun fetchTrendingMusicProjects(
        userGenres: String
    ): List<TrendingMusicProject> = withContext(Dispatchers.IO) {
        ErrorReportingService.logBreadcrumb("fetchTrendingMusicProjects invoked with genres: $userGenres")
        val now = System.currentTimeMillis()
        try {
            val dao = com.example.onboarding.database.RepositoryProvider.discoverFeedDao
            val cachedEntities = dao.getValidFeedItemsSync(now)
            if (cachedEntities.isNotEmpty()) {
                ErrorReportingService.logBreadcrumb("Serving ${cachedEntities.size} Discover feed projects from Room Database cache")
                return@withContext cachedEntities.map { entity ->
                    TrendingMusicProject(
                        id = entity.id,
                        title = entity.title,
                        creatorName = entity.creatorName,
                        creatorRole = entity.creatorRole,
                        coverImageUrl = entity.coverImageUrl,
                        genre = entity.genre,
                        matchScore = entity.matchScore,
                        seekingRoles = entity.seekingRoles.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        description = entity.description,
                        audioSnippetTitle = entity.audioSnippetTitle,
                        listensCount = entity.listensCount
                    )
                }
            }
        } catch (e: Throwable) {
            ErrorReportingService.logWarning("GeminiEngine", "Room cache check failed: ${e.message}")
        }

        val projects = getFallbackTrendingProjects(userGenres)
        try {
            val dao = com.example.onboarding.database.RepositoryProvider.discoverFeedDao
            val entities = projects.map { proj ->
                com.example.onboarding.database.DiscoverFeedEntity(
                    id = proj.id,
                    title = proj.title,
                    creatorName = proj.creatorName,
                    creatorRole = proj.creatorRole,
                    coverImageUrl = proj.coverImageUrl,
                    genre = proj.genre,
                    matchScore = proj.matchScore,
                    seekingRoles = proj.seekingRoles.joinToString(","),
                    description = proj.description,
                    audioSnippetTitle = proj.audioSnippetTitle,
                    listensCount = proj.listensCount,
                    isSynced = false,
                    fetchedAt = now,
                    expiresAt = now + (24 * 60 * 60 * 1000)
                )
            }
            dao.insertAll(entities)
            dao.markItemsSynced(entities.map { it.id })
            ErrorReportingService.logBreadcrumb("Cached ${entities.size} Discover feed items into Room with 24h expiration policy")
        } catch (e: Throwable) {
            ErrorReportingService.logWarning("GeminiEngine", "Failed to cache discover feed in Room: ${e.message}")
        }

        return@withContext projects
    }

    fun getFallbackRecommendations(userGenres: String, userSkills: String): List<CollaboratorRecommendation> {
        return listOf(
            CollaboratorRecommendation(
                name = "Aarav Mehta",
                role = "Synthesizer & Modular Producer",
                location = "Bandra, Mumbai",
                matchScore = 98,
                projectIdea = "Ambient Synthwave EP Collaboration",
                synergyReason = "High genre alignment in ${userGenres.ifEmpty { "Indie Rock" }}. Aarav complements your $userSkills skills with deep analog synth patches.",
                genres = listOf("Synthwave", "Ambient", "Indie"),
                avatarEmoji = "🎹"
            ),
            CollaboratorRecommendation(
                name = "Aditi Sen",
                role = "Jazz & Soul Topline Vocalist",
                location = "Indiranagar, Bengaluru",
                matchScore = 95,
                projectIdea = "Acoustic Fusion Track Vocal Feature",
                synergyReason = "Aditi's rich vocal timbre bridges well with your $userSkills work. Perfect fit for live acoustic sessions.",
                genres = listOf("Neo-Soul", "Jazz", "Acoustic"),
                avatarEmoji = "🎤"
            ),
            CollaboratorRecommendation(
                name = "Vikramaditya Roy",
                role = "Sitar Virtuoso & Raga Innovator",
                location = "Hauz Khas, Delhi",
                matchScore = 92,
                projectIdea = "Cinematic Classical-Electronic Score",
                synergyReason = "Combines traditional Indian raga motifs with modern electronic production and beats.",
                genres = listOf("Classical Fusion", "Cinematic", "Raga"),
                avatarEmoji = "🪕"
            )
        )
    }

    fun getFallbackTrendingProjects(userGenres: String): List<TrendingMusicProject> {
        val primaryGenre = userGenres.split(",").firstOrNull()?.trim() ?: "Indie & Electronic"
        return listOf(
            TrendingMusicProject(
                id = "proj_1",
                title = "Echoes of Malabar - Folk Fusion EP",
                creatorName = "Kabeer Varma",
                creatorRole = "Audio Engineer & Producer",
                coverImageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
                genre = primaryGenre,
                matchScore = 97,
                seekingRoles = listOf("Sitarist", "Mix Engineer", "Male Lead Vocals"),
                description = "Combining traditional Kerala folk percussion with deep sub bass synth lines and atmospheric reverb vocals.",
                audioSnippetTitle = "Malabar_Groove_RoughMix_v2.wav",
                listensCount = "14.2k"
            ),
            TrendingMusicProject(
                id = "proj_2",
                title = "Neon Nocturne Vol. 3",
                creatorName = "Rhea Kapoor",
                creatorRole = "Composer & Electronic Artist",
                coverImageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
                genre = "Synthwave / Cyberpop",
                matchScore = 94,
                seekingRoles = listOf("Electric Guitar Soloist", "Lyricist"),
                description = "Night drive synthwave soundscapes with analog synthesizers. Finalizing 4th track for summer EP release.",
                audioSnippetTitle = "Cyber_Sunset_Preview_Demo.mp3",
                listensCount = "28.9k"
            ),
            TrendingMusicProject(
                id = "proj_3",
                title = "Raag Bhairavi Reimagined",
                creatorName = "Tarun Pandit",
                creatorRole = "Classical Instrumentalist",
                coverImageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
                genre = "Indian Classical Fusion",
                matchScore = 91,
                seekingRoles = listOf("Tabla Player", "Mastering Engineer"),
                description = "Morning raga session combined with cinematic string arrangements and modern lo-fi beats.",
                audioSnippetTitle = "Bhairavi_LoFi_Stem_Take4.flac",
                listensCount = "9.8k"
            ),
            TrendingMusicProject(
                id = "proj_4",
                title = "Indie Acoustic Sessions - Chapter 2",
                creatorName = "Maya Sharma",
                creatorRole = "Singer-Songwriter",
                coverImageUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=600&auto=format&fit=crop&q=80",
                genre = "Indie Folk / Acoustic",
                matchScore = 89,
                seekingRoles = listOf("Cello Player", "Harmony Vocalist"),
                description = "Warm intimate acoustic guitar record recorded at Churchgate Attic Studio. Looking for string embellishments.",
                audioSnippetTitle = "Breeze_Vocal_Take1.mp3",
                listensCount = "19.4k"
            )
        )
    }
}
