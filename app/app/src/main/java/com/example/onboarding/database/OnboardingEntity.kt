package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "onboarding_profile")
data class OnboardingEntity(
    @PrimaryKey val id: String = "current_user",
    val mobileNumber: String = "",
    val isOtpVerified: Boolean = false,
    val primaryRole: String? = null,
    val secondaryRole: String? = null,
    val selectedLanguages: String = "", // Comma-separated list
    val selectedGenres: String = "",    // Comma-separated list
    val selectedInstruments: String = "", // Comma-separated list
    val state: String = "",
    val city: String = "",
    val area: String = "",
    val fullName: String = "",
    val displayName: String = "",
    val bio: String = "",
    val selectedAvatarIndex: Int = 0,
    val profilePicturePath: String? = null,
    val coverPicturePath: String? = null,
    val currentScreenName: String = "WELCOME",
    val email: String = "",
    val dateOfBirth: String = "",
    val gender: String = ""
)
