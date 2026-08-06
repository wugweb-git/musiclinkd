package com.example.auth

import android.util.Log
import com.example.onboarding.util.AnalyticsHelper
import com.example.onboarding.util.ErrorReportingService
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser?, val uid: String, val email: String) : AuthState()
    data class Unauthenticated(val reason: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class FirebaseAuthManager private constructor() {

    companion object {
        private const val TAG = "FirebaseAuthManager"
        val instance: FirebaseAuthManager by lazy { FirebaseAuthManager() }
    }

    private val _currentAuthState = MutableStateFlow<AuthState>(AuthState.Idle)
    val currentAuthState: StateFlow<AuthState> = _currentAuthState

    init {
        listenToAuthState()
    }

    private fun getAuth(): FirebaseAuth? {
        return try {
            val isFirebaseReady = try { FirebaseApp.getInstance() != null } catch (e: Throwable) { false }
            if (isFirebaseReady) {
                FirebaseAuth.getInstance()
            } else null
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseAuth instance not available: ${e.message}")
            null
        }
    }

    private fun listenToAuthState() {
        val auth = getAuth()
        if (auth == null) {
            _currentAuthState.value = AuthState.Unauthenticated("Firebase Auth offline mode")
            return
        }

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                val email = user.email ?: "user@musiclinkd.com"
                _currentAuthState.value = AuthState.Authenticated(user, user.uid, email)
                ErrorReportingService.setUserContext(user.uid, user.email, "AUTHENTICATED_USER")
                Log.i(TAG, "Auth state changed: User authenticated (UID: ${user.uid}) [Project: ${AnalyticsHelper.PRIMARY_PROJECT_ID}]")
            } else {
                _currentAuthState.value = AuthState.Unauthenticated()
                Log.i(TAG, "Auth state changed: User signed out")
            }
        }
    }

    fun observeUser(): Flow<FirebaseUser?> = callbackFlow {
        val auth = getAuth()
        if (auth == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun registerUser(email: String, pass: String): Result<String> {
        _currentAuthState.value = AuthState.Loading
        val auth = getAuth()
        if (auth == null) {
            // Emulated registration for offline environment
            val mockUid = "local_uid_${email.hashCode()}"
            _currentAuthState.value = AuthState.Authenticated(null, mockUid, email)
            return Result.success(mockUid)
        }

        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                _currentAuthState.value = AuthState.Authenticated(user, uid, email)
                AnalyticsHelper.trackUserInteraction("user_registered", mapOf("uid" to uid, "project_id" to AnalyticsHelper.PRIMARY_PROJECT_ID))
                Result.success(uid)
            } else {
                _currentAuthState.value = AuthState.Error("Registration succeeded but user profile was null")
                Result.failure(Exception("Null user received on registration"))
            }
        } catch (e: Throwable) {
            val errMsg = e.localizedMessage ?: "Failed to register account"
            _currentAuthState.value = AuthState.Error(errMsg)
            ErrorReportingService.logException(e, tag = "FirebaseAuthRegistration", customKeys = mapOf("email" to email))
            Result.failure(e)
        }
    }

    suspend fun signInUser(email: String, pass: String): Result<String> {
        _currentAuthState.value = AuthState.Loading
        val auth = getAuth()
        if (auth == null) {
            // Emulated sign-in for offline environment
            val mockUid = "local_uid_${email.hashCode()}"
            _currentAuthState.value = AuthState.Authenticated(null, mockUid, email)
            return Result.success(mockUid)
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                _currentAuthState.value = AuthState.Authenticated(user, uid, email)
                AnalyticsHelper.trackUserInteraction("user_signed_in", mapOf("uid" to uid, "project_id" to AnalyticsHelper.PRIMARY_PROJECT_ID))
                Result.success(uid)
            } else {
                _currentAuthState.value = AuthState.Error("Sign in succeeded but user profile was null")
                Result.failure(Exception("Null user received on sign in"))
            }
        } catch (e: Throwable) {
            val errMsg = e.localizedMessage ?: "Failed to sign in"
            _currentAuthState.value = AuthState.Error(errMsg)
            ErrorReportingService.logException(e, tag = "FirebaseAuthSignIn", customKeys = mapOf("email" to email))
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val auth = getAuth() ?: return Result.success(Unit)
        return try {
            auth.sendPasswordResetEmail(email).await()
            AnalyticsHelper.trackUserInteraction("password_reset_sent", mapOf("email" to email))
            Result.success(Unit)
        } catch (e: Throwable) {
            ErrorReportingService.logException(e, tag = "FirebaseAuthPasswordReset", customKeys = mapOf("email" to email))
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            getAuth()?.signOut()
            _currentAuthState.value = AuthState.Unauthenticated("User initiated sign-out")
            Log.i(TAG, "User signed out successfully")
        } catch (e: Throwable) {
            Log.e(TAG, "Error during sign out: ${e.message}")
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return getAuth()?.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return getAuth()?.currentUser != null
    }
}
