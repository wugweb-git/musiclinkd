package com.example.onboarding.database

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CurrentUser(
    val id: String = "current_user",
    val profilePhoto: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
    val displayName: String = "Vedanshu Srivastava",
    val handle: String = "vedanshu_music",
    val bio: String = "Passionate sound architect based in Mumbai. Specializing in cinematic fusion, synth layering, and spatial audio production.",
    val location: String = "Mumbai, Maharashtra",
    val roles: List<String> = listOf("Music Producer", "Composer"),
    val stats: Map<String, String> = mapOf(
        "followers" to "2,480",
        "responseRate" to "98%",
        "rating" to "4.9",
        "credits" to "34 Verified"
    ),
    val availability: String = "Available"
)

object CurrentUserProvider {
    private val _currentUser = MutableStateFlow(CurrentUser())
    val currentUser: StateFlow<CurrentUser> = _currentUser.asStateFlow()

    fun updateProfile(
        displayName: String? = null,
        handle: String? = null,
        bio: String? = null,
        location: String? = null,
        profilePhoto: String? = null,
        roles: List<String>? = null,
        availability: String? = null
    ) {
        val current = _currentUser.value
        _currentUser.value = current.copy(
            displayName = displayName ?: current.displayName,
            handle = handle ?: current.handle,
            bio = bio ?: current.bio,
            location = location ?: current.location,
            profilePhoto = profilePhoto ?: current.profilePhoto,
            roles = roles ?: current.roles,
            availability = availability ?: current.availability
        )
    }

    fun syncWithEntity(entity: UserProfileEntity) {
        val rolesList = entity.skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        updateProfile(
            displayName = entity.displayName,
            handle = entity.professionalName,
            bio = entity.bio,
            location = entity.location,
            profilePhoto = entity.profilePicturePath ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
            roles = if (rolesList.isEmpty()) listOf(entity.headline) else rolesList
        )
    }

    fun getSessionStatus(): String {
        return "authenticated"
    }
}
