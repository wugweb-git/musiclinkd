package com.example.onboarding.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.onboarding.database.RepositoryProvider
import com.example.onboarding.ui.screens.ChatContext
import com.example.onboarding.ui.screens.ConvoItem
import com.example.onboarding.ui.screens.MessageBubbleItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MessagingViewModel : ViewModel() {

    private val repository = RepositoryProvider.chatRepository

    val conversations: StateFlow<List<ConvoItem>> = repository.allConversations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedConvoId = MutableStateFlow<String?>(null)
    val selectedConvoId: StateFlow<String?> = _selectedConvoId.asStateFlow()

    // Retrieve active messages in real-time from SQLite database using Flow!
    val activeMessages: StateFlow<List<MessageBubbleItem>> = _selectedConvoId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getMessages(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var webSocket: WebSocket? = null
    private val okHttpClient = OkHttpClient()

    init {
        createNotificationChannel()
        seedDatabaseIfNeeded()
        connectWebSocket()
    }

    private fun seedDatabaseIfNeeded() {
        viewModelScope.launch {
            // Wait for conversations to load, if empty seed default mock database entries
            val list = conversations.filter { it.isNotEmpty() }.firstOrNull() ?: emptyList()
            if (list.isEmpty()) {
                val defaultConvos = listOf(
                    ConvoItem(
                        id = "studio_1",
                        name = "SoundOne Studios",
                        role = "Premium Mixing Suite",
                        lastMessage = "Hey Kabir, your booking request for July 12 is secure-booking locked. See you then!",
                        timestamp = "10:24 AM",
                        unreadCount = 2,
                        context = ChatContext.STUDIO,
                        contextStatus = "Booking Confirmed",
                        isPinned = true
                    ),
                    ConvoItem(
                        id = "market_1",
                        name = "Aria Vox",
                        role = "Vintage Gear Seller",
                        lastMessage = "I can do ₹1,15,000 if you buy the vintage Gibson today. Secured transaction is ready.",
                        timestamp = "Yesterday",
                        unreadCount = 0,
                        context = ChatContext.MARKETPLACE,
                        contextStatus = "Offer Pending"
                    ),
                    ConvoItem(
                        id = "opp_1",
                        name = "Netflix India Music",
                        role = "Music Licensing Guild",
                        lastMessage = "We reviewed your electronic backing track demo. Please attach your complete FLAC portfolio.",
                        timestamp = "Jun 26",
                        unreadCount = 1,
                        context = ChatContext.OPPORTUNITY,
                        contextStatus = "Under Review",
                        isPinned = true
                    ),
                    ConvoItem(
                        id = "project_1",
                        name = "Kabir & The Band",
                        role = "EP Collaboration Project",
                        lastMessage = "Track 4 stem mixing is almost done. I've uploaded the drum transient wav file.",
                        timestamp = "Jun 24",
                        unreadCount = 0,
                        context = ChatContext.PROJECT,
                        contextStatus = "60% Completed"
                    )
                )
                repository.insertConversations(defaultConvos)

                val messagesMap = mapOf(
                    "studio_1" to listOf(
                        MessageBubbleItem("s1_1", "SoundOne Studios", "Welcome to SoundOne. We offer dual SSL 9000J recording suites.", "Yesterday", false),
                        MessageBubbleItem("s1_2", "Me", "Hey, I need to book the primary tracking suite for vocal session stems.", "Yesterday", true),
                        MessageBubbleItem("s1_3", "SoundOne Studios", "Perfect. Let's create a secure booking block for July 12.", "Yesterday", false),
                        MessageBubbleItem("s1_4", "System", "Booking Contract Proposed", "10:00 AM", false, type = "booking", metaTitle = "Vocal Suite A Block", metaDesc = "July 12, 2:00 PM (4 Hours)", metaPrice = "₹12,000", metaStatus = "PENDING"),
                        MessageBubbleItem("s1_5", "SoundOne Studios", "I've locked the calendar hold. You can accept and pay the secure deposit directly in the header action.", "10:22 AM", false),
                        MessageBubbleItem("s1_6", "System", "Invoice Generated", "10:23 AM", false, type = "document", metaTitle = "INV-2026-894.pdf", metaDesc = "Booking Invoice (4 Hours Room Hire)", metaPrice = "₹12,000", metaStatus = "UNPAID")
                    ),
                    "market_1" to listOf(
                        MessageBubbleItem("m1_1", "Aria Vox", "Hello! Here is the custom listing for the Gibson guitar.", "Yesterday", false),
                        MessageBubbleItem("m1_2", "System", "Gibson Les Paul Standard 1978", "Yesterday", false, type = "marketplace", metaTitle = "Vintage Gibson Les Paul 1978", metaDesc = "Excellent condition, original fretboard, low action, hard-shell case included.", metaPrice = "₹1,20,000", metaStatus = "AVAILABLE"),
                        MessageBubbleItem("m1_3", "Me", "Can we do ₹1,10,000 with secure shipping?", "Yesterday", true),
                        MessageBubbleItem("m1_4", "Aria Vox", "I can do ₹1,15,000 if you buy the vintage Gibson today. Secured transaction is ready.", "Yesterday", false)
                    ),
                    "opp_1" to listOf(
                        MessageBubbleItem("o1_1", "Netflix India Music", "Hello. We are sourcing heavy synth & Indian ambient hybrid scores for an upcoming high-budget web-series.", "Jun 26", false),
                        MessageBubbleItem("o1_2", "System", "Music Licensing Opportunity", "Jun 26", false, type = "opportunity", metaTitle = "Sci-Fi Ambient Hybrid Theme", metaDesc = "Netflix Originals Division · 3 Tracks needed.", metaPrice = "₹1,50,000 Budget", metaStatus = "APPLIED"),
                        MessageBubbleItem("o1_3", "Netflix India Music", "We reviewed your electronic backing track demo. Please attach your complete FLAC portfolio.", "Jun 26", false)
                    ),
                    "project_1" to listOf(
                        MessageBubbleItem("p1_1", "Kabir", "Hey crew! Welcome to the workspace. Let's list the milestones.", "Jun 24", false),
                        MessageBubbleItem("p1_2", "System", "EP 'Chasing Dreams' Collaboration Board", "Jun 24", false, type = "project", metaTitle = "Chasing Dreams EP", metaDesc = "Milestone 3/5: Mix mastering dry vocal tracking stems.", metaPrice = "Due July 30", metaStatus = "ACTIVE"),
                        MessageBubbleItem("p1_3", "Me", "I am working on mixing Track 4 vocals now.", "Jun 24", true),
                        MessageBubbleItem("p1_4", "System", "Vocals_Stem_Dry_T4.wav", "Jun 24", false, type = "audio", metaTitle = "Vocals_Stem_Dry_T4.wav", metaDesc = "Duration: 3:45 · 24-bit PCM audio", metaPrice = "128 MB", metaStatus = "PLAYABLE")
                    )
                )

                messagesMap.forEach { (convoId, msgs) ->
                    msgs.forEach { msg ->
                        repository.insertMessage(convoId, msg)
                    }
                }
            }
        }
    }

    fun selectConversation(convoId: String) {
        _selectedConvoId.value = convoId
        viewModelScope.launch {
            val list = conversations.value
            val convo = list.find { it.id == convoId }
            if (convo != null && convo.unreadCount > 0) {
                repository.insertConversation(convo.copy(unreadCount = 0))
            }
        }
    }

    fun deselectConversation() {
        _selectedConvoId.value = null
    }

    private fun connectWebSocket() {
        val request = Request.Builder()
            .url("wss://echo.websocket.org")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Connection opened successfully!
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Incoming real WebSocket message received!
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Fall back to typing-simulation loops seamlessly
            }
        })
    }

    fun sendMessage(text: String, type: String = "text", title: String = "", desc: String = "", price: String = "", status: String = "") {
        val activeConvoId = _selectedConvoId.value ?: return
        val msgId = "msg_${System.currentTimeMillis()}"
        val newMsg = MessageBubbleItem(
            id = msgId,
            sender = "Me",
            text = text,
            time = "Just Now",
            isMe = true,
            type = type,
            metaTitle = title,
            metaDesc = desc,
            metaPrice = price,
            metaStatus = status
        )

        viewModelScope.launch {
            repository.insertMessage(activeConvoId, newMsg)

            val convo = conversations.value.find { it.id == activeConvoId }
            convo?.let {
                repository.insertConversation(it.copy(lastMessage = text, timestamp = "Just Now"))
            }

            webSocket?.send("echo:$activeConvoId:$text")
            
            triggerSimulatedReply(activeConvoId, text)
        }
    }

    private fun triggerSimulatedReply(convoId: String, userText: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Realistic typing delay

            val convo = conversations.value.find { it.id == convoId } ?: return@launch
            val replyText = when (convoId) {
                "studio_1" -> when {
                    userText.contains("book", true) || userText.contains("reserve", true) -> 
                        "Got your hold request. Booking transaction is verified. Studio A engineer has been notified."
                    userText.contains("price", true) || userText.contains("rate", true) -> 
                        "Our rate is ₹3,000/hr, but for a 4-hour block booking we can offer 10% off!"
                    else -> "Perfect Kabir! If you need to upload backing tracks or guide vocals, upload them directly via Files."
                }
                "market_1" -> when {
                    userText.contains("price", true) || userText.contains("discount", true) -> 
                        "That Gibson Les Paul is an absolute collector piece. I can do ₹1,12,000 if you close the secure deposit now."
                    else -> "Great choice. Packing in original hard-shell case. Secure courier is on standby!"
                }
                "opp_1" -> when {
                    userText.contains("apply", true) || userText.contains("submit", true) -> 
                        "Thank you. Your license submission for Netflix Originals Sci-Fi series is queued."
                    else -> "We've received your FLAC audio sample. The production team is analyzing the key signature and dynamic range."
                }
                "project_1" -> when {
                    userText.contains("mix", true) || userText.contains("master", true) -> 
                        "The dry vocals sound crisp, Kabir. Let's apply a light plate reverb and send the revision."
                    else -> "Excellent! Stems are looking solid. Let's connect for a video review on Thursday."
                }
                else -> "Message received! Let's collaborate soon."
            }

            val replyMsg = MessageBubbleItem(
                id = "reply_${System.currentTimeMillis()}",
                sender = convo.name,
                text = replyText,
                time = "Just Now",
                isMe = false,
                type = "text"
            )

            repository.insertMessage(convoId, replyMsg)

            val isCurrentChatOpen = _selectedConvoId.value == convoId
            val unreadIncrement = if (isCurrentChatOpen) 0 else 1
            repository.insertConversation(
                convo.copy(
                    lastMessage = replyText,
                    timestamp = "Just Now",
                    unreadCount = convo.unreadCount + unreadIncrement
                )
            )

            if (!isCurrentChatOpen) {
                showPushNotification(convo.name, replyText)
            }
        }
    }

    private fun createNotificationChannel() {
        val context = RepositoryProvider.appContext ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MusicLinkd Messages"
            val descriptionText = "Real-time updates and direct messages from other creators"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("musiclinkd_chat", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @android.annotation.SuppressLint("NotificationPermission")
    private fun showPushNotification(sender: String, messageText: String) {
        val context = RepositoryProvider.appContext ?: return
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(context, "musiclinkd_chat")
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(sender)
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateConvoStatus(status: String) {
        val activeConvoId = _selectedConvoId.value ?: return
        viewModelScope.launch {
            val convo = conversations.value.find { it.id == activeConvoId }
            convo?.let {
                repository.insertConversation(it.copy(contextStatus = status))
            }
        }
    }

    fun togglePin(convoId: String) {
        viewModelScope.launch {
            val convo = conversations.value.find { it.id == convoId }
            convo?.let {
                repository.insertConversation(it.copy(isPinned = !it.isPinned))
            }
        }
    }

    fun deleteChat(convoId: String) {
        viewModelScope.launch {
            repository.deleteConversation(convoId)
            if (_selectedConvoId.value == convoId) {
                _selectedConvoId.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            webSocket?.close(1000, "ViewModel cleared")
        } catch (e: Exception) {
            // Safe teardown
        }
    }
}
