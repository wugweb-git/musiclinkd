package com.example.onboarding.database

import com.example.onboarding.ui.screens.ReputationReviewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRepository(private val reviewDao: ReviewDao) {

    val allReviews: Flow<List<ReputationReviewItem>> = reviewDao.getAllReviews().map { entities ->
        entities.map { entity ->
            ReputationReviewItem(
                id = entity.id,
                reviewerName = entity.reviewerName,
                reviewerRole = entity.reviewerRole,
                isVerified = entity.isVerified,
                ratingStars = entity.ratingStars,
                reviewText = entity.reviewText,
                projectContext = entity.projectContext,
                date = entity.date,
                initialHelpfulCount = entity.helpfulCount,
                hasAudioSample = entity.hasAudioSample,
                audioTitle = entity.audioTitle,
                hasImages = entity.hasImages,
                businessReplyText = entity.businessReplyText
            )
        }
    }

    suspend fun insertReview(item: ReputationReviewItem) {
        reviewDao.insertReview(
            ReviewEntity(
                id = item.id,
                reviewerName = item.reviewerName,
                reviewerRole = item.reviewerRole,
                isVerified = item.isVerified,
                ratingStars = item.ratingStars,
                reviewText = item.reviewText,
                projectContext = item.projectContext,
                date = item.date,
                helpfulCount = item.initialHelpfulCount,
                hasAudioSample = item.hasAudioSample,
                audioTitle = item.audioTitle,
                hasImages = item.hasImages,
                businessReplyText = item.businessReplyText
            )
        )
    }

    suspend fun insertReviews(items: List<ReputationReviewItem>) {
        reviewDao.insertReviews(items.map { item ->
            ReviewEntity(
                id = item.id,
                reviewerName = item.reviewerName,
                reviewerRole = item.reviewerRole,
                isVerified = item.isVerified,
                ratingStars = item.ratingStars,
                reviewText = item.reviewText,
                projectContext = item.projectContext,
                date = item.date,
                helpfulCount = item.initialHelpfulCount,
                hasAudioSample = item.hasAudioSample,
                audioTitle = item.audioTitle,
                hasImages = item.hasImages,
                businessReplyText = item.businessReplyText
            )
        })
    }

    suspend fun updateReview(item: ReputationReviewItem) {
        reviewDao.updateReview(
            ReviewEntity(
                id = item.id,
                reviewerName = item.reviewerName,
                reviewerRole = item.reviewerRole,
                isVerified = item.isVerified,
                ratingStars = item.ratingStars,
                reviewText = item.reviewText,
                projectContext = item.projectContext,
                date = item.date,
                helpfulCount = item.initialHelpfulCount,
                hasAudioSample = item.hasAudioSample,
                audioTitle = item.audioTitle,
                hasImages = item.hasImages,
                businessReplyText = item.businessReplyText
            )
        )
    }

    suspend fun deleteAll() {
        reviewDao.deleteAll()
    }
}
