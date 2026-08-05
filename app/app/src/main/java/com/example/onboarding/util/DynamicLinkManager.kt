package com.example.onboarding.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Firebase Dynamic Links Manager for MusicLinkd.
 * Enables seamless creation and sharing of dynamic links for user profiles and music projects.
 * Supports deep link parsing and reflection/fallback handling when Firebase SDK is invoked.
 */
object DynamicLinkManager {

    private const val TAG = "DynamicLinkManager"
    const val DOMAIN_PREFIX = "https://musiclinkd.page.link"
    const val BASE_APP_URL = "https://musiclinkd.com"

    /**
     * Generate a Firebase Dynamic Link URL for a user profile.
     */
    fun generateProfileDynamicLink(
        userId: String,
        userName: String,
        userRole: String
    ): String {
        val deepLink = "$BASE_APP_URL/profile?id=${Uri.encode(userId)}&name=${Uri.encode(userName)}"
        return try {
            // Construct Firebase Dynamic Link format
            "$DOMAIN_PREFIX/?link=${Uri.encode(deepLink)}&apn=com.example.onboarding&st=${Uri.encode("MusicLinkd Profile - $userName")}&sd=${Uri.encode("Collaborate with $userName ($userRole) on MusicLinkd!")}&si=https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"
        } catch (e: Throwable) {
            ErrorReportingService.logException(e, "DynamicLinks", "Error building profile dynamic link")
            deepLink
        }
    }

    /**
     * Generate a Firebase Dynamic Link URL for a specific music project.
     */
    fun generateProjectDynamicLink(
        projectId: String,
        projectTitle: String,
        creatorName: String
    ): String {
        val deepLink = "$BASE_APP_URL/project?id=${Uri.encode(projectId)}&title=${Uri.encode(projectTitle)}"
        return try {
            "$DOMAIN_PREFIX/?link=${Uri.encode(deepLink)}&apn=com.example.onboarding&st=${Uri.encode("Music Project - $projectTitle")}&sd=${Uri.encode("Listen and join $creatorName's track project on MusicLinkd!")}&si=https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300"
        } catch (e: Throwable) {
            ErrorReportingService.logException(e, "DynamicLinks", "Error building project dynamic link")
            deepLink
        }
    }

    /**
     * Launch native share intent for a user profile with Firebase Dynamic Link.
     */
    fun shareProfileDynamicLink(
        context: Context,
        userId: String,
        userName: String,
        userRole: String
    ) {
        val dynamicLink = generateProfileDynamicLink(userId, userName, userRole)
        val shareMessage = """
            Connect with $userName on MusicLinkd!
            Role: $userRole
            
            Click the link to view profile, STEMs, and send collaboration requests:
            $dynamicLink
        """.trimIndent()

        ErrorReportingService.logBreadcrumb("Sharing profile dynamic link for $userId ($userName)")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "MusicLinkd Profile: $userName")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share MusicLinkd Profile"))
    }

    /**
     * Launch native share intent for a music project with Firebase Dynamic Link.
     */
    fun shareProjectDynamicLink(
        context: Context,
        projectId: String,
        projectTitle: String,
        creatorName: String
    ) {
        val dynamicLink = generateProjectDynamicLink(projectId, projectTitle, creatorName)
        val shareMessage = """
            Check out this music project on MusicLinkd!
            "$projectTitle" by $creatorName
            
            Listen to audio stems, view open collaborator roles, and join the session:
            $dynamicLink
        """.trimIndent()

        ErrorReportingService.logBreadcrumb("Sharing project dynamic link for $projectId ($projectTitle)")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Music Project: $projectTitle")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Music Project"))
    }

    /**
     * Handle incoming dynamic link intent and extract target profile or project parameters.
     */
    fun handleIncomingIntent(
        intent: Intent?,
        onProfileResolved: (userId: String) -> Unit,
        onProjectResolved: (projectId: String) -> Unit
    ) {
        val data: Uri? = intent?.data
        if (data != null) {
            val linkParam = data.getQueryParameter("link")
            val targetUri = if (!linkParam.isNullOrEmpty()) Uri.parse(linkParam) else data

            when {
                targetUri.path?.contains("/profile") == true -> {
                    val id = targetUri.getQueryParameter("id") ?: "vedanshu_srivastava"
                    ErrorReportingService.logBreadcrumb("Resolved dynamic link for profile: $id")
                    onProfileResolved(id)
                }
                targetUri.path?.contains("/project") == true -> {
                    val id = targetUri.getQueryParameter("id") ?: "proj_1"
                    ErrorReportingService.logBreadcrumb("Resolved dynamic link for project: $id")
                    onProjectResolved(id)
                }
            }
        }
    }
}
