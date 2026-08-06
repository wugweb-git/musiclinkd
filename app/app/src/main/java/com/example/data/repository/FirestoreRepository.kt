package com.example.data.repository

import android.util.Log
import com.example.data.models.Artist
import com.example.data.models.Event
import com.example.data.models.Venue
import com.example.onboarding.util.AnalyticsHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository private constructor() {

    companion object {
        private const val TAG = "FirestoreRepository"
        val instance: FirestoreRepository by lazy { FirestoreRepository() }

        private const val COLLECTION_ARTISTS = "artists"
        private const val COLLECTION_VENUES = "venues"
        private const val COLLECTION_EVENTS = "events"
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            val isFirebaseReady = try { FirebaseApp.getInstance() != null } catch (e: Throwable) { false }
            if (isFirebaseReady) {
                FirebaseFirestore.getInstance()
            } else null
        } catch (e: Throwable) {
            Log.w(TAG, "Firestore instance not available: ${e.message}")
            null
        }
    }

    // --- ARTIST CRUD OPERATIONS ---

    suspend fun createArtist(artist: Artist): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            val docRef = if (artist.id.isEmpty()) {
                firestore.collection(COLLECTION_ARTISTS).document()
            } else {
                firestore.collection(COLLECTION_ARTISTS).document(artist.id)
            }
            val finalArtist = artist.copy(
                id = docRef.id,
                projectId = AnalyticsHelper.PRIMARY_PROJECT_ID,
                createdAt = artist.createdAt ?: com.google.firebase.Timestamp.now(),
                updatedAt = com.google.firebase.Timestamp.now()
            )
            docRef.set(finalArtist).await()
            Log.i(TAG, "Created artist with ID: ${docRef.id}")
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error creating artist: ${e.message}")
            false
        }
    }

    suspend fun getArtistById(artistId: String): Artist? {
        val firestore = getFirestore() ?: return null
        return try {
            val snapshot = firestore.collection(COLLECTION_ARTISTS).document(artistId).get().await()
            snapshot.toObject(Artist::class.java)
        } catch (e: Throwable) {
            Log.e(TAG, "Error fetching artist $artistId: ${e.message}")
            null
        }
    }

    fun observeArtists(): Flow<List<Artist>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_ARTISTS)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Observe artists error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val artists = snapshot?.documents?.mapNotNull { it.toObject(Artist::class.java) } ?: emptyList()
                trySend(artists)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateArtist(artist: Artist): Boolean {
        if (artist.id.isEmpty()) return false
        val firestore = getFirestore() ?: return false
        return try {
            val updated = artist.copy(updatedAt = com.google.firebase.Timestamp.now())
            firestore.collection(COLLECTION_ARTISTS).document(artist.id).set(updated).await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error updating artist ${artist.id}: ${e.message}")
            false
        }
    }

    suspend fun deleteArtist(artistId: String): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            firestore.collection(COLLECTION_ARTISTS).document(artistId).delete().await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error deleting artist $artistId: ${e.message}")
            false
        }
    }

    // --- VENUE CRUD OPERATIONS ---

    suspend fun createVenue(venue: Venue): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            val docRef = if (venue.id.isEmpty()) {
                firestore.collection(COLLECTION_VENUES).document()
            } else {
                firestore.collection(COLLECTION_VENUES).document(venue.id)
            }
            val finalVenue = venue.copy(
                id = docRef.id,
                projectId = AnalyticsHelper.PRIMARY_PROJECT_ID,
                createdAt = venue.createdAt ?: com.google.firebase.Timestamp.now()
            )
            docRef.set(finalVenue).await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error creating venue: ${e.message}")
            false
        }
    }

    suspend fun getVenueById(venueId: String): Venue? {
        val firestore = getFirestore() ?: return null
        return try {
            val snapshot = firestore.collection(COLLECTION_VENUES).document(venueId).get().await()
            snapshot.toObject(Venue::class.java)
        } catch (e: Throwable) {
            Log.e(TAG, "Error fetching venue $venueId: ${e.message}")
            null
        }
    }

    fun observeVenues(): Flow<List<Venue>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_VENUES)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val venues = snapshot?.documents?.mapNotNull { it.toObject(Venue::class.java) } ?: emptyList()
                trySend(venues)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateVenue(venue: Venue): Boolean {
        if (venue.id.isEmpty()) return false
        val firestore = getFirestore() ?: return false
        return try {
            firestore.collection(COLLECTION_VENUES).document(venue.id).set(venue).await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error updating venue ${venue.id}: ${e.message}")
            false
        }
    }

    suspend fun deleteVenue(venueId: String): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            firestore.collection(COLLECTION_VENUES).document(venueId).delete().await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error deleting venue $venueId: ${e.message}")
            false
        }
    }

    // --- EVENT CRUD OPERATIONS ---

    suspend fun createEvent(event: Event): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            val docRef = if (event.id.isEmpty()) {
                firestore.collection(COLLECTION_EVENTS).document()
            } else {
                firestore.collection(COLLECTION_EVENTS).document(event.id)
            }
            val finalEvent = event.copy(
                id = docRef.id,
                projectId = AnalyticsHelper.PRIMARY_PROJECT_ID,
                createdAt = event.createdAt ?: com.google.firebase.Timestamp.now()
            )
            docRef.set(finalEvent).await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error creating event: ${e.message}")
            false
        }
    }

    suspend fun getEventById(eventId: String): Event? {
        val firestore = getFirestore() ?: return null
        return try {
            val snapshot = firestore.collection(COLLECTION_EVENTS).document(eventId).get().await()
            snapshot.toObject(Event::class.java)
        } catch (e: Throwable) {
            Log.e(TAG, "Error fetching event $eventId: ${e.message}")
            null
        }
    }

    fun observeEvents(): Flow<List<Event>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_EVENTS)
            .orderBy("eventDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val events = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) } ?: emptyList()
                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateEvent(event: Event): Boolean {
        if (event.id.isEmpty()) return false
        val firestore = getFirestore() ?: return false
        return try {
            firestore.collection(COLLECTION_EVENTS).document(event.id).set(event).await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error updating event ${event.id}: ${e.message}")
            false
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            firestore.collection(COLLECTION_EVENTS).document(eventId).delete().await()
            true
        } catch (e: Throwable) {
            Log.e(TAG, "Error deleting event $eventId: ${e.message}")
            false
        }
    }
}
