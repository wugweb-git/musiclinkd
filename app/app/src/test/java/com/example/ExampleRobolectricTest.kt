package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.onboarding.database.*
import com.example.onboarding.models.OnboardingScreen
import com.example.onboarding.models.OnboardingState
import com.example.onboarding.ui.screens.ChatContext
import com.example.onboarding.ui.screens.ConvoItem
import com.example.onboarding.ui.screens.MessageBubbleItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: MusicLinkdDatabase
  private lateinit var chatDao: ChatDao
  private lateinit var onboardingDao: OnboardingDao
  private lateinit var reviewDao: ReviewDao
  private lateinit var chatRepository: ChatRepository
  private lateinit var onboardingRepository: OnboardingRepository
  private lateinit var reviewRepository: ReviewRepository

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, MusicLinkdDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    chatDao = database.chatDao()
    onboardingDao = database.onboardingDao()
    reviewDao = database.reviewDao()
    chatRepository = ChatRepository(chatDao)
    onboardingRepository = OnboardingRepository(onboardingDao)
    reviewRepository = ReviewRepository(reviewDao)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MusicLinkd", appName)
  }

  @Test
  fun `verify MusicianService constructor and fields`() {
    val service = com.example.onboarding.ui.screens.MusicianService(
      name = "Dolby Atmos Mastering",
      pricing = "₹12,000",
      rateType = "per Track",
      description = "Full spatial spatial mix and mastering with certified stems."
    )
    assertEquals("Dolby Atmos Mastering", service.name)
    assertEquals("₹12,000", service.pricing)
    assertEquals("per Track", service.rateType)
    assertEquals("Full spatial spatial mix and mastering with certified stems.", service.description)
  }

  @Test
  fun `verify ReputationReviewItem constructor and fields`() {
    val review = com.example.onboarding.ui.screens.ReputationReviewItem(
      id = "rev-test-1",
      reviewerName = "Sarah Connor",
      reviewerRole = "Creative Director at Synergy Games",
      isVerified = true,
      ratingStars = 5,
      reviewText = "Incredible cinematic arrangements.",
      projectContext = "Project: Cyberpunk 2088 Game Soundtrack",
      date = "June 18, 2026",
      initialHelpfulCount = 14,
      hasAudioSample = true,
      audioTitle = "Cyberpunk_Synth_Master.wav"
    )
    assertEquals("rev-test-1", review.id)
    assertEquals("Sarah Connor", review.reviewerName)
    assertEquals("Creative Director at Synergy Games", review.reviewerRole)
    assertEquals(true, review.isVerified)
    assertEquals(5, review.ratingStars)
    assertEquals("Incredible cinematic arrangements.", review.reviewText)
    assertEquals("Project: Cyberpunk 2088 Game Soundtrack", review.projectContext)
    assertEquals("June 18, 2026", review.date)
    assertEquals(14, review.initialHelpfulCount)
    assertEquals(true, review.hasAudioSample)
    assertEquals("Cyberpunk_Synth_Master.wav", review.audioTitle)
  }

  @Test
  fun `test onboarding state serialization and deserialization in Room`() = runBlocking {
    val sampleState = OnboardingState(
        currentScreen = OnboardingScreen.LOCATION,
        mobileNumber = "9876543210",
        primaryRole = "Vocalist",
        secondaryRole = "Lyricist",
        selectedLanguages = setOf("Hindi", "English"),
        selectedGenres = setOf("Electronic", "Indian Classical"),
        selectedInstruments = setOf("Acoustic Guitar"),
        state = "Maharashtra",
        city = "Mumbai",
        area = "Bandra West",
        fullName = "Kabir Dev",
        displayName = "KabirDevMusic",
        bio = "Independent vocalist and songwriter looking for professional mixing collaborations.",
        selectedAvatarIndex = 3,
        isOtpVerified = true
    )

    onboardingRepository.saveState(sampleState)

    val retrieved = onboardingRepository.getOnboardingProfile()
    assertNotNull(retrieved)
    assertEquals(OnboardingScreen.LOCATION, retrieved!!.currentScreen)
    assertEquals("9876543210", retrieved.mobileNumber)
    assertEquals("Vocalist", retrieved.primaryRole)
    assertEquals("Lyricist", retrieved.secondaryRole)
    assertTrue(retrieved.selectedLanguages.contains("Hindi"))
    assertTrue(retrieved.selectedLanguages.contains("English"))
    assertTrue(retrieved.selectedGenres.contains("Electronic"))
    assertTrue(retrieved.selectedInstruments.contains("Acoustic Guitar"))
    assertEquals("Maharashtra", retrieved.state)
    assertEquals("Mumbai", retrieved.city)
    assertEquals("Bandra West", retrieved.area)
    assertEquals("Kabir Dev", retrieved.fullName)
    assertEquals("KabirDevMusic", retrieved.displayName)
    assertEquals("Independent vocalist and songwriter looking for professional mixing collaborations.", retrieved.bio)
    assertEquals(3, retrieved.selectedAvatarIndex)
    assertTrue(retrieved.isOtpVerified)
  }

  @Test
  fun `test conversation insertion and list ordering in Room`() = runBlocking {
    val convo1 = ConvoItem(
        id = "convo_1",
        name = "SoundOne Studios",
        role = "Mixing Studio",
        lastMessage = "Let's lock July 12.",
        timestamp = "10:00 AM",
        unreadCount = 2,
        context = ChatContext.STUDIO,
        contextStatus = "Active Hold",
        isPinned = false,
        avatarUrl = ""
    )
    val convo2 = ConvoItem(
        id = "convo_2",
        name = "Aria Vox",
        role = "Guitarist",
        lastMessage = "Guitar tracks are done.",
        timestamp = "11:00 AM",
        unreadCount = 0,
        context = ChatContext.PROJECT,
        contextStatus = "Completed",
        isPinned = true,
        avatarUrl = ""
    )

    chatRepository.insertConversations(listOf(convo1, convo2))

    val allConvos = chatRepository.allConversations.first()
    assertEquals(2, allConvos.size)
    // convo2 should be first because isPinned is true, which places pinned items on top!
    assertEquals("convo_2", allConvos[0].id)
    assertTrue(allConvos[0].isPinned)
    assertEquals("convo_1", allConvos[1].id)
    assertEquals(false, allConvos[1].isPinned)
  }

  @Test
  fun `test conversation messages addition and retrieval`() = runBlocking {
    val message1 = MessageBubbleItem(
        id = "msg_1",
        sender = "SoundOne Studios",
        text = "Hello Kabir!",
        time = "10:00 AM",
        isMe = false
    )
    val message2 = MessageBubbleItem(
        id = "msg_2",
        sender = "Me",
        text = "Hey! S stems look great.",
        time = "10:05 AM",
        isMe = true
    )

    chatRepository.insertMessage("convo_1", message1)
    chatRepository.insertMessage("convo_1", message2)

    val messages = chatRepository.getMessages("convo_1").first()
    assertEquals(2, messages.size)
    assertEquals("msg_1", messages[0].id)
    assertEquals("SoundOne Studios", messages[0].sender)
    assertEquals("Hello Kabir!", messages[0].text)
    assertEquals(false, messages[0].isMe)

    assertEquals("msg_2", messages[1].id)
    assertEquals("Me", messages[1].sender)
    assertEquals("Hey! S stems look great.", messages[1].text)
    assertEquals(true, messages[1].isMe)
  }

  @Test
  fun `test conversation and messages deletion`() = runBlocking {
    val convo = ConvoItem(
        id = "convo_to_del",
        name = "Temp Artist",
        role = "Beatmaker",
        lastMessage = "Let's test delete.",
        timestamp = "9:00 AM",
        unreadCount = 1,
        context = ChatContext.MARKETPLACE,
        contextStatus = "Offer",
        isPinned = false,
        avatarUrl = ""
    )
    val msg = MessageBubbleItem(
        id = "msg_del",
        sender = "Temp Artist",
        text = "Delete me.",
        time = "9:00 AM",
        isMe = false
    )

    chatRepository.insertConversation(convo)
    chatRepository.insertMessage("convo_to_del", msg)

    // Verify they exist
    val initialConvos = chatRepository.allConversations.first()
    assertTrue(initialConvos.any { it.id == "convo_to_del" })
    val initialMsgs = chatRepository.getMessages("convo_to_del").first()
    assertEquals(1, initialMsgs.size)

    // Delete
    chatRepository.deleteConversation("convo_to_del")

    // Verify they are gone
    val remainingConvos = chatRepository.allConversations.first()
    assertTrue(remainingConvos.none { it.id == "convo_to_del" })
    val remainingMsgs = chatRepository.getMessages("convo_to_del").first()
    assertTrue(remainingMsgs.isEmpty())
  }

  @Test
  fun `smoke test app configuration and layout basics`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertNotNull(context)
    
    // Smoke check application package name
    val packageName = context.packageName
    assertTrue(packageName.startsWith("com.example") || packageName.startsWith("com.aistudio"))

    // Smoke check that we can load basic assets/drawables if configured or strings
    val appLabel = context.applicationInfo.loadLabel(context.packageManager).toString()
    assertNotNull(appLabel)
  }

  @Test
  fun `crud test reviews repository operations`() = runBlocking {
    val reviewItem = com.example.onboarding.ui.screens.ReputationReviewItem(
        id = "review_crud_id_1",
        reviewerName = "Jane Doe",
        reviewerRole = "Producer",
        isVerified = true,
        ratingStars = 5,
        reviewText = "Amazing service and quality mix!",
        projectContext = "Mastering Session",
        date = "July 05, 2026",
        initialHelpfulCount = 5,
        hasAudioSample = false,
        audioTitle = "",
        hasImages = false,
        businessReplyText = ""
    )

    // 1. Create (Insert)
    reviewRepository.insertReview(reviewItem)

    // 2. Read (Query)
    var allReviews = reviewRepository.allReviews.first()
    assertEquals(1, allReviews.size)
    assertEquals("Jane Doe", allReviews[0].reviewerName)
    assertEquals("Amazing service and quality mix!", allReviews[0].reviewText)
    assertEquals(5, allReviews[0].ratingStars)

    // 3. Update
    val updatedReview = reviewItem.copy(
        reviewerName = "Jane Smith",
        reviewText = "Updated Mix Review!",
        initialHelpfulCount = 6,
        businessReplyText = "Thanks Jane!"
    )
    reviewRepository.updateReview(updatedReview)

    allReviews = reviewRepository.allReviews.first()
    assertEquals(1, allReviews.size)
    assertEquals("Jane Smith", allReviews[0].reviewerName)
    assertEquals("Updated Mix Review!", allReviews[0].reviewText)
    assertEquals(6, allReviews[0].initialHelpfulCount)
    assertEquals("Thanks Jane!", allReviews[0].businessReplyText)

    // 4. Delete
    reviewRepository.deleteAll()
    allReviews = reviewRepository.allReviews.first()
    assertTrue(allReviews.isEmpty())
  }
}

