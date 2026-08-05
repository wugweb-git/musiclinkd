package com.example.onboarding.database

import com.example.onboarding.models.OnboardingScreen
import com.example.onboarding.models.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingRepository(private val onboardingDao: OnboardingDao) {

    val onboardingStateFlow: Flow<OnboardingState?> = onboardingDao.getOnboardingProfileFlow().map { entity ->
        entity?.let {
            OnboardingState(
                currentScreen = try { OnboardingScreen.valueOf(it.currentScreenName) } catch (e: Exception) { OnboardingScreen.WELCOME },
                mobileNumber = it.mobileNumber,
                otp = "",
                primaryRole = it.primaryRole,
                secondaryRole = it.secondaryRole,
                selectedLanguages = it.selectedLanguages.split(",").filter { s -> s.isNotEmpty() }.toSet(),
                selectedGenres = it.selectedGenres.split(",").filter { s -> s.isNotEmpty() }.toSet(),
                selectedInstruments = it.selectedInstruments.split(",").filter { s -> s.isNotEmpty() }.toSet(),
                state = it.state,
                city = it.city,
                area = it.area,
                fullName = it.fullName,
                displayName = it.displayName,
                email = it.email,
                dateOfBirth = it.dateOfBirth,
                gender = it.gender,
                bio = it.bio,
                selectedAvatarIndex = it.selectedAvatarIndex,
                profilePicturePath = it.profilePicturePath,
                coverPicturePath = it.coverPicturePath,
                isOtpVerified = it.isOtpVerified,
                otpError = null,
                mobileError = null
            )
        }
    }

    suspend fun getOnboardingProfile(): OnboardingState? {
        val entity = onboardingDao.getOnboardingProfile() ?: return null
        return OnboardingState(
            currentScreen = try { OnboardingScreen.valueOf(entity.currentScreenName) } catch (e: Exception) { OnboardingScreen.WELCOME },
            mobileNumber = entity.mobileNumber,
            otp = "",
            primaryRole = entity.primaryRole,
            secondaryRole = entity.secondaryRole,
            selectedLanguages = entity.selectedLanguages.split(",").filter { s -> s.isNotEmpty() }.toSet(),
            selectedGenres = entity.selectedGenres.split(",").filter { s -> s.isNotEmpty() }.toSet(),
            selectedInstruments = entity.selectedInstruments.split(",").filter { s -> s.isNotEmpty() }.toSet(),
            state = entity.state,
            city = entity.city,
            area = entity.area,
            fullName = entity.fullName,
            displayName = entity.displayName,
            email = entity.email,
            dateOfBirth = entity.dateOfBirth,
            gender = entity.gender,
            bio = entity.bio,
            selectedAvatarIndex = entity.selectedAvatarIndex,
            profilePicturePath = entity.profilePicturePath,
            coverPicturePath = entity.coverPicturePath,
            isOtpVerified = entity.isOtpVerified,
            otpError = null,
            mobileError = null
        )
    }

    suspend fun saveState(state: OnboardingState) {
        onboardingDao.saveOnboardingProfile(
            OnboardingEntity(
                id = "current_user",
                mobileNumber = state.mobileNumber,
                isOtpVerified = state.isOtpVerified,
                primaryRole = state.primaryRole,
                secondaryRole = state.secondaryRole,
                selectedLanguages = state.selectedLanguages.joinToString(","),
                selectedGenres = state.selectedGenres.joinToString(","),
                selectedInstruments = state.selectedInstruments.joinToString(","),
                state = state.state,
                city = state.city,
                area = state.area,
                fullName = state.fullName,
                displayName = state.displayName,
                email = state.email,
                dateOfBirth = state.dateOfBirth,
                gender = state.gender,
                bio = state.bio,
                selectedAvatarIndex = state.selectedAvatarIndex,
                profilePicturePath = state.profilePicturePath,
                coverPicturePath = state.coverPicturePath,
                currentScreenName = state.currentScreen.name
            )
        )
    }

    suspend fun clear() {
        onboardingDao.clear()
    }
}
