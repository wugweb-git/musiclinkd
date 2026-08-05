package com.example.onboarding.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository interface for User Profile management, decoupling UI & ViewModels from data sources.
 */
interface UserProfileRepository {
    fun getUserProfileFlow(id: String = "current_user"): Flow<UserProfileEntity?>
    suspend fun getUserProfile(id: String = "current_user"): UserProfileEntity?
    suspend fun saveUserProfile(profile: UserProfileEntity)
    suspend fun updateHeadshot(id: String = "current_user", headshotPath: String)
    suspend fun updateProfessionalDetails(
        id: String = "current_user",
        fullName: String,
        role: String,
        subRole: String,
        genres: List<String>,
        bio: String,
        hourlyRate: String
    )
}

/**
 * Room Database implementation of [UserProfileRepository].
 */
class RoomUserProfileRepository(private val dao: UserProfileDao) : UserProfileRepository {
    override fun getUserProfileFlow(id: String): Flow<UserProfileEntity?> {
        return dao.getUserProfileFlow(id)
    }

    override suspend fun getUserProfile(id: String): UserProfileEntity? {
        return dao.getUserProfile(id)
    }

    override suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    override suspend fun updateHeadshot(id: String, headshotPath: String) {
        val existing = dao.getUserProfile(id) ?: UserProfileEntity(id = id, displayName = "Professional User")
        val updated = existing.copy(profilePicturePath = headshotPath)
        dao.saveUserProfile(updated)
    }

    override suspend fun updateProfessionalDetails(
        id: String,
        fullName: String,
        role: String,
        subRole: String,
        genres: List<String>,
        bio: String,
        hourlyRate: String
    ) {
        val existing = dao.getUserProfile(id) ?: UserProfileEntity(id = id, displayName = fullName)
        val updated = existing.copy(
            displayName = fullName,
            headline = role,
            bio = bio,
            genres = genres.joinToString(", "),
            hourlyRate = hourlyRate
        )
        dao.saveUserProfile(updated)
    }
}

/**
 * In-Memory Stub Repository for offline testing and fast flow validation.
 */
class StubUserProfileRepository(
    initialProfile: UserProfileEntity = UserProfileEntity(
        id = "current_user",
        displayName = "Aarav Mehta",
        professionalName = "Aarav Beats",
        headline = "Music Producer & Engineer",
        bio = "Award-winning music producer specializing in Bollywood & Pop fusion.",
        genres = "Pop, Bollywood, EDM, Indie Folk",
        location = "Mumbai, Maharashtra",
        profilePicturePath = null,
        hourlyRate = "₹10,000 / track"
    )
) : UserProfileRepository {
    private val profileState = MutableStateFlow<UserProfileEntity?>(initialProfile)

    override fun getUserProfileFlow(id: String): Flow<UserProfileEntity?> = profileState.asStateFlow()

    override suspend fun getUserProfile(id: String): UserProfileEntity? = profileState.value

    override suspend fun saveUserProfile(profile: UserProfileEntity) {
        profileState.value = profile
    }

    override suspend fun updateHeadshot(id: String, headshotPath: String) {
        profileState.value = profileState.value?.copy(profilePicturePath = headshotPath)
    }

    override suspend fun updateProfessionalDetails(
        id: String,
        fullName: String,
        role: String,
        subRole: String,
        genres: List<String>,
        bio: String,
        hourlyRate: String
    ) {
        profileState.value = profileState.value?.copy(
            displayName = fullName,
            headline = role,
            bio = bio,
            genres = genres.joinToString(", "),
            hourlyRate = hourlyRate
        )
    }
}
