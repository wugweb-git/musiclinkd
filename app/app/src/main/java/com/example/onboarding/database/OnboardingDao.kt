package com.example.onboarding.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OnboardingDao {
    @Query("SELECT * FROM onboarding_profile WHERE id = 'current_user'")
    fun getOnboardingProfileFlow(): Flow<OnboardingEntity?>

    @Query("SELECT * FROM onboarding_profile WHERE id = 'current_user'")
    suspend fun getOnboardingProfile(): OnboardingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOnboardingProfile(profile: OnboardingEntity)

    @Query("DELETE FROM onboarding_profile")
    suspend fun clear()
}
