package com.example.onboarding.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentUrl: String? = null

    private val _isPlayingState = MutableStateFlow(false)
    val isPlayingState: StateFlow<Boolean> = _isPlayingState

    private val _currentTrackTitle = MutableStateFlow("No track loaded")
    val currentTrackTitle: StateFlow<String> = _currentTrackTitle

    fun play(context: Context, url: String, title: String) {
        try {
            if (currentUrl == url && mediaPlayer != null) {
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    _isPlayingState.value = true
                }
                return
            }

            // Stop current playback
            stop()

            currentUrl = url
            _currentTrackTitle.value = title

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlayingState.value = true
                    Toast.makeText(context, "Streaming: $title", Toast.LENGTH_SHORT).show()
                }
                setOnCompletionListener {
                    _isPlayingState.value = false
                    Toast.makeText(context, "Playback completed", Toast.LENGTH_SHORT).show()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "MediaPlayer error: $what, $extra")
                    _isPlayingState.value = false
                    Toast.makeText(context, "Failed to load audio stream", Toast.LENGTH_SHORT).show()
                    true
                }
                prepareAsync()
            }
            Toast.makeText(context, "Buffering high-fidelity track...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception playing audio", e)
            Toast.makeText(context, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                _isPlayingState.value = false
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception pausing audio", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            _isPlayingState.value = false
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception stopping audio", e)
        }
    }
}
