package com.example.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onboarding.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    // Countdown state for OTP resend
    private val _otpTimer = MutableStateFlow(59)
    val otpTimer: StateFlow<Int> = _otpTimer.asStateFlow()

    private var timerJob: Job? = null

    private var restoredScreen: OnboardingScreen = OnboardingScreen.WELCOME

    init {
        viewModelScope.launch {
            val repository = com.example.onboarding.database.RepositoryProvider.onboardingRepository
            val saved = repository.getOnboardingProfile()
            if (saved != null) {
                restoredScreen = if (saved.currentScreen == OnboardingScreen.SPLASH) OnboardingScreen.WELCOME else saved.currentScreen
                _state.value = saved.copy(currentScreen = OnboardingScreen.SPLASH)
            }
            
            // Listen to state changes and persist them to the database
            _state.collect { currentState ->
                val stateToSave = if (currentState.currentScreen == OnboardingScreen.SPLASH) {
                    currentState.copy(currentScreen = restoredScreen)
                } else {
                    currentState
                }
                repository.saveState(stateToSave)
            }
        }
    }

    fun updateMobileNumber(number: String) {
        // Strip non-digits
        val cleaned = number.filter { it.isDigit() }
        _state.update { 
            it.copy(
                mobileNumber = cleaned,
                mobileError = null
            ) 
        }
    }

    fun sendOtp() {
        val number = _state.value.mobileNumber
        if (number.length != 10) {
            _state.update { it.copy(mobileError = "Enter a valid 10-digit mobile number") }
            return
        }
        
        stopTimer()
        _state.update { it.copy(isOtpSending = true) }
        viewModelScope.launch {
            delay(1200) // Simulate SMS gateway OTP dispatch network latency
            _state.update { 
                it.copy(
                    isOtpSending = false,
                    currentScreen = OnboardingScreen.OTP_VERIFY
                ) 
            }
            startTimer()
        }
    }

    private fun startTimer() {
        _otpTimer.value = 59
        timerJob = viewModelScope.launch {
            while (_otpTimer.value > 0) {
                delay(1000)
                _otpTimer.update { it - 1 }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resendOtp() {
        if (_otpTimer.value == 0) {
            stopTimer()
            startTimer()
            // Reset OTP field
            _state.update { it.copy(otp = "", otpError = null) }
        }
    }

    fun updateOtp(otpCode: String) {
        val cleaned = otpCode.filter { it.isDigit() }.take(6)
        _state.update { it.copy(otp = cleaned, otpError = null) }
        
        // Auto submit if 6 digits are typed
        if (cleaned.length == 6) {
            verifyOtp()
        }
    }

    fun verifyOtp() {
        val enteredOtp = _state.value.otp
        if (enteredOtp == "123456" || enteredOtp.length == 6) {
            _state.update { it.copy(isOtpVerifying = true, otpError = null) }
            viewModelScope.launch {
                delay(1200) // Simulate secure security handshake/verification latency
                stopTimer()
                _state.update { 
                    it.copy(
                        isOtpVerifying = false,
                        isOtpVerified = true,
                        otpError = null,
                        currentScreen = OnboardingScreen.ROLE_PRIMARY
                    )
                }
            }
        } else {
            _state.update { it.copy(otpError = "Invalid verification code. Enter '123456' for test.") }
        }
    }

    fun selectPrimaryRole(role: String) {
        _state.update { 
            it.copy(
                primaryRole = role,
                // Automatically clear secondary if same is selected
                secondaryRole = if (it.secondaryRole == role) null else it.secondaryRole
            ) 
        }
    }

    fun confirmPrimaryRole() {
        if (_state.value.primaryRole != null) {
            _state.update { it.copy(currentScreen = OnboardingScreen.ROLE_SECONDARY) }
        }
    }

    fun selectSecondaryRole(role: String) {
        if (role == _state.value.primaryRole) return // cannot select primary as secondary
        _state.update { 
            it.copy(
                secondaryRole = if (it.secondaryRole == role) null else role
            ) 
        }
    }

    fun skipSecondaryRole() {
        _state.update { 
            it.copy(
                secondaryRole = null,
                currentScreen = OnboardingScreen.LANGUAGES
            )
        }
    }

    fun confirmSecondaryRole() {
        _state.update { it.copy(currentScreen = OnboardingScreen.LANGUAGES) }
    }

    fun toggleLanguage(lang: String) {
        _state.update {
            val updated = it.selectedLanguages.toMutableSet()
            if (updated.contains(lang)) {
                updated.remove(lang)
            } else {
                if (updated.size < 3) {
                    updated.add(lang)
                }
            }
            it.copy(selectedLanguages = updated, languagesError = null)
        }
    }

    fun confirmLanguages() {
        if (_state.value.selectedLanguages.isEmpty()) {
            _state.update { it.copy(languagesError = "Please select at least one language to proceed.") }
        } else {
            _state.update { it.copy(languagesError = null, currentScreen = OnboardingScreen.GENRES) }
        }
    }

    fun toggleGenre(genre: String) {
        _state.update {
            val updated = it.selectedGenres.toMutableSet()
            if (updated.contains(genre)) {
                updated.remove(genre)
            } else {
                if (updated.size < 3) {
                    updated.add(genre)
                }
            }
            it.copy(selectedGenres = updated, genresError = null)
        }
    }

    fun confirmGenres() {
        if (_state.value.selectedGenres.isEmpty()) {
            _state.update { it.copy(genresError = "Please select at least one music genre to proceed.") }
        } else {
            val canPlay = canRolePlayInstruments(_state.value.primaryRole)
            _state.update { 
                it.copy(
                    genresError = null,
                    currentScreen = if (canPlay) OnboardingScreen.INSTRUMENTS else OnboardingScreen.LOCATION
                ) 
            }
        }
    }

    fun toggleInstrument(inst: String) {
        _state.update {
            val updated = it.selectedInstruments.toMutableSet()
            if (updated.contains(inst)) {
                updated.remove(inst)
                val updatedProf = it.instrumentProficiency.toMutableMap()
                updatedProf.remove(inst)
                it.copy(selectedInstruments = updated, instrumentProficiency = updatedProf)
            } else {
                if (updated.size < 2) {
                    updated.add(inst)
                    val updatedProf = it.instrumentProficiency.toMutableMap()
                    updatedProf[inst] = SkillProficiency(
                        yearsOfExperience = "3-5 Years",
                        proficiencyLevel = "Intermediate",
                        portfolioProofUrl = ""
                    )
                    it.copy(selectedInstruments = updated, instrumentProficiency = updatedProf)
                } else {
                    it
                }
            }
        }
    }

    fun updateInstrumentProficiency(inst: String, level: String, years: String, proof: String) {
        _state.update {
            val updatedProf = it.instrumentProficiency.toMutableMap()
            updatedProf[inst] = SkillProficiency(
                yearsOfExperience = years,
                proficiencyLevel = level,
                portfolioProofUrl = proof
            )
            it.copy(instrumentProficiency = updatedProf)
        }
    }

    fun updateRoleExperience(level: String, years: String, proof: String) {
        _state.update {
            it.copy(
                roleExperience = SkillProficiency(
                    yearsOfExperience = years,
                    proficiencyLevel = level,
                    portfolioProofUrl = proof
                )
            )
        }
    }

    fun updateNetworkingLink(platform: String, url: String) {
        _state.update {
            val updatedLinks = it.networkingLinks.toMutableMap()
            if (url.trim().isEmpty()) {
                updatedLinks.remove(platform)
            } else {
                updatedLinks[platform] = url
            }
            it.copy(networkingLinks = updatedLinks)
        }
    }

    fun confirmInstruments() {
        _state.update { it.copy(currentScreen = OnboardingScreen.LOCATION) }
    }

    fun updateLocation(stateName: String, cityName: String, areaName: String) {
        _state.update {
            it.copy(
                state = stateName,
                city = cityName,
                area = areaName,
                stateError = null,
                cityError = null
            )
        }
    }

    fun confirmLocation() {
        // Enforce basic state and city choice for completeness, or default placeholder if blank to proceed simply
        val curState = _state.value.state.trim()
        val curCity = _state.value.city.trim()
        val curArea = _state.value.area.trim()

        var hasError = false
        var stateErr: String? = null
        var cityErr: String? = null

        if (curState.isNotEmpty() && !curState.all { it.isLetter() || it.isWhitespace() }) {
            stateErr = "State name must contain only letters."
            hasError = true
        }
        if (curCity.isNotEmpty() && !curCity.all { it.isLetter() || it.isWhitespace() }) {
            cityErr = "City name must contain only letters."
            hasError = true
        }

        if (hasError) {
            _state.update {
                it.copy(
                    stateError = stateErr,
                    cityError = cityErr
                )
            }
            return
        }
        
        _state.update { currentState ->
            val updatedState = currentState.copy(
                state = if (curState.isEmpty()) "Maharashtra" else currentState.state,
                city = if (curCity.isEmpty()) "Mumbai" else currentState.city,
                area = if (currentState.area.isEmpty()) "Andheri West" else currentState.area,
                stateError = null,
                cityError = null
            )

            // Dynamic pre-fill based on primaryRole if current inputs are blank
            if (updatedState.fullName.isEmpty() || updatedState.displayName.isEmpty()) {
                val pRole = updatedState.primaryRole ?: ""
                var mFullName = "Vedanshu Srivastava"
                var mDisplayName = "vedanshu"
                var mBio = "Independent composer. Mixing synthwave loops with Hindustani vocal arrangements."
                var mAvatar = 4

                when (pRole) {
                    "Singer" -> {
                        mFullName = "Vedanshu Srivastava"
                        mDisplayName = "vedanshu_vocals"
                        mBio = "Lead singer and vocalist. Specializing in classical Hindustani crossover and acoustic indie tracks."
                        mAvatar = 0
                    }
                    "Musician" -> {
                        mFullName = "Vedanshu Srivastava"
                        mDisplayName = "vedanshu_musician"
                        mBio = "Guitar virtuoso and multi-instrumentalist. Recording electric, acoustic, and traditional session arrangements."
                        mAvatar = 3
                    }
                    "Session Musician" -> {
                        mFullName = "Vedanshu Srivastava"
                        mDisplayName = "vedanshu_session"
                        mBio = "Professional percussionist and drummer. Specializing in organic Indian sub-beats and fusion rhythms."
                        mAvatar = 1
                    }
                    "Band" -> {
                        mFullName = "Vedanshu & Friends"
                        mDisplayName = "vedanshu_band"
                        mBio = "An organic blues and fusion music collective bringing acoustic strings and classical flute harmonies to live stages."
                        mAvatar = 1
                    }
                    "Composer" -> {
                        mFullName = "Vedanshu Srivastava"
                        mDisplayName = "vedanshu"
                        mBio = "Independent composer. Mixing synthwave loops with Hindustani vocal arrangements."
                        mAvatar = 4
                    }
                    "Music Director" -> {
                        mFullName = "Vedanshu Srivastava"
                        mDisplayName = "vedanshu_director"
                        mBio = "Soundtrack director and studio score arranger. Blending cinematic orchestration with modern synthesizers."
                        mAvatar = 4
                    }
                    "Lyricist" -> {
                        mFullName = "Irshad Jamil"
                        mDisplayName = "irshad_lyrix"
                        mBio = "Songwriter and Urdu/Hindi poet crafting authentic folk narratives, cinematic singalongs, and poetic ghazals."
                        mAvatar = 5
                    }
                    "Producer" -> {
                        mFullName = "Karan Kanchan"
                        mDisplayName = "kanchan_beats"
                        mBio = "Music producer and beatmaker crafting heavy independent hip-hop anthems and futuristic Lo-Fi loops."
                        mAvatar = 1
                    }
                    "Sound Engineer" -> {
                        mFullName = "Siddharth Swar"
                        mDisplayName = "siddharth_mix"
                        mBio = "SSL 9000J certified mix-mastering engineer. Carving pristine spatial width and standard-setting clarity."
                        mAvatar = 2
                    }
                    "DJ" -> {
                        mFullName = "DJ Rish"
                        mDisplayName = "dj_rish_goa"
                        mBio = "Anjuna sunset resident DJ. Blending gold analog synthesizers with progressive organic soundscapes."
                        mAvatar = 2
                    }
                    "Studio" -> {
                        mFullName = "Swar Recording Studio"
                        mDisplayName = "swar_studios"
                        mBio = "Premier regional live studio room featuring classical acoustic wood architecture, high-spec inputs, and vintage Neumann tube mics."
                        mAvatar = 4
                    }
                    "Music Label" -> {
                        mFullName = "Ironwood Records"
                        mDisplayName = "ironwood_music"
                        mBio = "Independent music distribution and publishing catalog represents 40+ regional rock and electronic artists."
                        mAvatar = 1
                    }
                    "Artist Manager" -> {
                        mFullName = "Nisha Rao"
                        mDisplayName = "nisha_manages"
                        mBio = "Talent manager representing top-tier indie acts and coordinating international showcase tour logistics."
                        mAvatar = 5
                    }
                    "Event Organizer" -> {
                        mFullName = "Nirmal Chawla"
                        mDisplayName = "nirmal_shows"
                        mBio = "Live show promoter and festival curator. Designing immersive regional indie circuits and live arenas."
                        mAvatar = 2
                    }
                }

                updatedState.copy(
                    fullName = mFullName,
                    displayName = mDisplayName,
                    bio = mBio,
                    selectedAvatarIndex = mAvatar,
                    currentScreen = OnboardingScreen.PROFILE_CREATION
                )
            } else {
                updatedState.copy(currentScreen = OnboardingScreen.PROFILE_CREATION)
            }
        }
    }

    fun updateProfile(
        fullName: String,
        displayName: String,
        bioText: String,
        avatarIndex: Int,
        profilePath: String? = _state.value.profilePicturePath,
        coverPath: String? = _state.value.coverPicturePath
    ) {
        _state.update {
            it.copy(
                fullName = fullName,
                displayName = displayName,
                bio = bioText,
                selectedAvatarIndex = avatarIndex,
                profilePicturePath = profilePath,
                coverPicturePath = coverPath,
                nameError = null,
                usernameError = null,
                bioError = null
            )
        }
    }

    fun confirmProfile() {
        val curName = _state.value.fullName.trim()
        val curUsername = _state.value.displayName.trim()
        val curBio = _state.value.bio.trim()
        val roleProofUrl = _state.value.roleExperience.portfolioProofUrl.trim()
        val netLinks = _state.value.networkingLinks

        // Check if user is trying to progress on completely empty fields:
        // We can prefill to make it frictionless but still validate if they edit.
        if (curName.isEmpty() && curUsername.isEmpty() && curBio.isEmpty()) {
            _state.update { it.copy(isProfileSaving = true) }
            viewModelScope.launch {
                delay(1500) // Simulate cloud persistence storage latency
                _state.update { currentState ->
                    val pRole = currentState.primaryRole ?: ""
                    var mFullName = "Vedanshu Srivastava"
                    var mDisplayName = "vedanshu"
                    var mBio = "Independent composer. Mixing synthwave loops with Hindustani vocal arrangements."
                    var mAvatar = 4

                    when (pRole) {
                        "Singer" -> {
                            mFullName = "Vedanshu Srivastava"
                            mDisplayName = "vedanshu_vocals"
                            mBio = "Lead singer and vocalist. Specializing in classical Hindustani crossover and acoustic indie tracks."
                            mAvatar = 0
                        }
                        "Musician" -> {
                            mFullName = "Vedanshu Srivastava"
                            mDisplayName = "vedanshu_musician"
                            mBio = "Guitar virtuoso and multi-instrumentalist. Recording electric, acoustic, and traditional session arrangements."
                            mAvatar = 3
                        }
                        "Session Musician" -> {
                            mFullName = "Vedanshu Srivastava"
                            mDisplayName = "vedanshu_session"
                            mBio = "Professional percussionist and drummer. Specializing in organic Indian sub-beats and fusion rhythms."
                            mAvatar = 1
                        }
                        "Band" -> {
                            mFullName = "Vedanshu & Friends"
                            mDisplayName = "vedanshu_band"
                            mBio = "An organic blues and fusion music collective bringing acoustic strings and classical flute harmonies to live stages."
                            mAvatar = 1
                        }
                        "Composer" -> {
                            mFullName = "Vedanshu Srivastava"
                            mDisplayName = "vedanshu"
                            mBio = "Independent composer. Mixing synthwave loops with Hindustani vocal arrangements."
                            mAvatar = 4
                        }
                        "Music Director" -> {
                            mFullName = "Vedanshu Srivastava"
                            mDisplayName = "vedanshu_director"
                            mBio = "Soundtrack director and studio score arranger. Blending cinematic orchestration with modern synthesizers."
                            mAvatar = 4
                        }
                        "Lyricist" -> {
                            mFullName = "Irshad Jamil"
                            mDisplayName = "irshad_lyrix"
                            mBio = "Songwriter and Urdu/Hindi poet crafting authentic folk narratives, cinematic singalongs, and poetic ghazals."
                            mAvatar = 5
                        }
                        "Producer" -> {
                            mFullName = "Karan Kanchan"
                            mDisplayName = "kanchan_beats"
                            mBio = "Music producer and beatmaker crafting heavy independent hip-hop anthems and futuristic Lo-Fi loops."
                            mAvatar = 1
                        }
                        "Sound Engineer" -> {
                            mFullName = "Siddharth Swar"
                            mDisplayName = "siddharth_mix"
                            mBio = "SSL 9000J certified mix-mastering engineering specialist. Carving pristine spatial width and standard-setting clarity."
                            mAvatar = 2
                        }
                        "DJ" -> {
                            mFullName = "DJ Rish"
                            mDisplayName = "dj_rish_goa"
                            mBio = "Anjuna sunset resident DJ. Blending gold analog synthesizers with progressive organic soundscapes."
                            mAvatar = 2
                        }
                        "Studio" -> {
                            mFullName = "Swar Recording Studio"
                            mDisplayName = "swar_studios"
                            mBio = "Premier regional live studio room featuring classical acoustic wood architecture, high-spec inputs, and vintage Neumann tube mics."
                            mAvatar = 4
                        }
                        "Music Label" -> {
                            mFullName = "Ironwood Records"
                            mDisplayName = "ironwood_music"
                            mBio = "Independent music distribution and publishing catalog represents 40+ regional rock and electronic artists."
                            mAvatar = 1
                        }
                        "Artist Manager" -> {
                            mFullName = "Nisha Rao"
                            mDisplayName = "nisha_manages"
                            mBio = "Talent manager representing top-tier indie acts and coordinating international showcase tour logistics."
                            mAvatar = 5
                        }
                        "Event Organizer" -> {
                            mFullName = "Nirmal Chawla"
                            mDisplayName = "nirmal_shows"
                            mBio = "Live show promoter and festival curator. Designing immersive regional indie circuits and live arenas."
                            mAvatar = 2
                        }
                    }

                    currentState.copy(
                        fullName = mFullName,
                        displayName = mDisplayName,
                        bio = mBio,
                        selectedAvatarIndex = mAvatar,
                        nameError = null,
                        usernameError = null,
                        bioError = null,
                        isProfileSaving = false,
                        currentScreen = OnboardingScreen.SUCCESS
                    )
                }
            }
            return
        }

        var hasError = false
        var nameErr: String? = null
        var usernameErr: String? = null
        var bioErr: String? = null
        var proofErr: String? = null
        val linksErrs = mutableMapOf<String, String>()

        // 1. Full Name Validation
        if (curName.isEmpty()) {
            nameErr = "Full Name is required."
            hasError = true
        } else if (curName.length < 3) {
            nameErr = "Full Name must be at least 3 characters."
            hasError = true
        } else if (!curName.all { it.isLetter() || it.isWhitespace() }) {
            nameErr = "Full Name must contain only letters and spaces."
            hasError = true
        }

        // 2. Display Handle Validation
        if (curUsername.isEmpty()) {
            usernameErr = "Display Handle is required."
            hasError = true
        } else if (curUsername.length < 3) {
            usernameErr = "Display Handle must be at least 3 characters."
            hasError = true
        } else if (curUsername.startsWith("_")) {
            usernameErr = "Display Handle cannot start with an underscore."
            hasError = true
        }

        // 3. Bio Validation
        if (curBio.isEmpty()) {
            bioErr = "Short Bio is required."
            hasError = true
        } else if (curBio.length < 10) {
            bioErr = "Short Bio must be at least 10 characters."
            hasError = true
        }

        // 4. Primary Role Proof URL Validation
        if (roleProofUrl.isNotEmpty()) {
            if (!roleProofUrl.startsWith("http://") && !roleProofUrl.startsWith("https://")) {
                proofErr = "Must start with http:// or https://"
                hasError = true
            } else if (!roleProofUrl.contains(".")) {
                proofErr = "Please enter a valid URL."
                hasError = true
            }
        }

        // 5. Social Links Validation
        netLinks.forEach { (platform, url) ->
            val trimmedUrl = url.trim()
            if (trimmedUrl.isNotEmpty()) {
                if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
                    linksErrs[platform] = "Must start with http:// or https://"
                    hasError = true
                } else if (!trimmedUrl.contains(".")) {
                    linksErrs[platform] = "Invalid URL."
                    hasError = true
                }
            }
        }

        if (hasError) {
            _state.update {
                it.copy(
                    nameError = nameErr,
                    usernameError = usernameErr,
                    bioError = bioErr,
                    proofError = proofErr,
                    linksErrors = linksErrs
                )
            }
            return
        }

        _state.update { it.copy(isProfileSaving = true) }
        viewModelScope.launch {
            val stateValue = _state.value
            val userId = com.example.onboarding.database.FirebaseManager.getAuth()?.currentUser?.uid ?: "local_uid_${stateValue.email.hashCode()}"
            val profileMap = mapOf(
                "mobileNumber" to stateValue.mobileNumber,
                "primaryRole" to (stateValue.primaryRole ?: ""),
                "secondaryRole" to (stateValue.secondaryRole ?: ""),
                "selectedLanguages" to stateValue.selectedLanguages.toList(),
                "selectedGenres" to stateValue.selectedGenres.toList(),
                "selectedInstruments" to stateValue.selectedInstruments.toList(),
                "country" to stateValue.country,
                "state" to stateValue.state,
                "city" to stateValue.city,
                "area" to stateValue.area,
                "fullName" to stateValue.fullName,
                "displayName" to stateValue.displayName,
                "email" to stateValue.email,
                "dateOfBirth" to stateValue.dateOfBirth,
                "gender" to stateValue.gender,
                "bio" to stateValue.bio,
                "selectedAvatarIndex" to stateValue.selectedAvatarIndex,
                "createdAt" to System.currentTimeMillis()
            )

            com.example.onboarding.database.FirebaseManager.saveUserProfileToFirestore(
                userId = userId,
                profileData = profileMap,
                onSuccess = {
                    _state.update {
                        it.copy(
                            nameError = null,
                            usernameError = null,
                            bioError = null,
                            proofError = null,
                            linksErrors = emptyMap(),
                            isProfileSaving = false,
                            currentScreen = OnboardingScreen.SUCCESS
                        )
                    }
                },
                onFailure = { errorMsg ->
                    _state.update {
                        it.copy(
                            isProfileSaving = false,
                            nameError = "Failed to sync to cloud: $errorMsg. Local database preserved."
                        )
                    }
                }
            )
        }
    }

    fun finishOnboarding() {
        _state.update { it.copy(currentScreen = OnboardingScreen.DASHBOARD) }
    }

    fun startEmailAuth() {
        _state.update {
            it.copy(
                currentScreen = OnboardingScreen.EMAIL_AUTH,
                firebaseAuthError = null,
                isFirebaseAuthLoading = false
            )
        }
    }

    fun updateFirebaseEmail(email: String) {
        _state.update { it.copy(firebaseEmailInput = email, firebaseAuthError = null) }
    }

    fun updateFirebasePassword(password: String) {
        _state.update { it.copy(firebasePasswordInput = password, firebaseAuthError = null) }
    }

    fun toggleFirebaseAuthMode() {
        _state.update { 
            it.copy(
                isFirebaseAuthLoginMode = !it.isFirebaseAuthLoginMode,
                firebaseAuthError = null
            ) 
        }
    }

    fun performFirebaseAuth(onComplete: () -> Unit) {
        val email = _state.value.firebaseEmailInput.trim()
        val password = _state.value.firebasePasswordInput
        
        if (email.isEmpty()) {
            _state.update { it.copy(firebaseAuthError = "Email field cannot be empty.") }
            return
        }
        if (password.isEmpty()) {
            _state.update { it.copy(firebaseAuthError = "Password field cannot be empty.") }
            return
        }
        
        _state.update { it.copy(isFirebaseAuthLoading = true, firebaseAuthError = null) }
        
        val successHandler: (String) -> Unit = { uid ->
            _state.update {
                it.copy(
                    isFirebaseAuthLoading = false,
                    isOtpVerified = true, // Bypass mobile step
                    email = email, // Set profile email
                    fullName = email.substringBefore("@").replaceFirstChar { c -> c.uppercase() }, // Pre-fill default name
                    currentScreen = OnboardingScreen.ROLE_PRIMARY
                )
            }
            onComplete()
        }
        
        val failureHandler: (String) -> Unit = { errMsg ->
            _state.update {
                it.copy(
                    isFirebaseAuthLoading = false,
                    firebaseAuthError = errMsg
                )
            }
        }
        
        if (_state.value.isFirebaseAuthLoginMode) {
            com.example.onboarding.database.FirebaseManager.loginFirebaseUser(
                email = email,
                password = password,
                onSuccess = successHandler,
                onFailure = failureHandler
            )
        } else {
            com.example.onboarding.database.FirebaseManager.createFirebaseUser(
                email = email,
                password = password,
                onSuccess = successHandler,
                onFailure = failureHandler
            )
        }
    }

    fun startMobileAuth() {
        _state.update { it.copy(currentScreen = OnboardingScreen.MOBILE_AUTH) }
    }

    fun signUpWithGoogle() {
        stopTimer()
        _state.update {
            it.copy(
                mobileNumber = "9876543210",
                isOtpVerified = true,
                fullName = "Vedanshu Srivastava",
                displayName = "vedanshu_google",
                email = "vedanshu.srivastava@gmail.com",
                dateOfBirth = "1998-05-12",
                gender = "Male",
                selectedAvatarIndex = 4, // Purplish V letter avatar
                currentScreen = OnboardingScreen.ROLE_PRIMARY
            )
        }
    }

    fun authenticateWithGoogleFirebase(idToken: String, onComplete: (Boolean, String) -> Unit) {
        stopTimer()
        _state.update { it.copy(isFirebaseAuthLoading = true, firebaseAuthError = null) }
        com.example.onboarding.database.FirebaseManager.signInWithGoogleToken(
            idToken = idToken,
            onSuccess = { uid, email, displayName ->
                _state.update {
                    it.copy(
                        isFirebaseAuthLoading = false,
                        mobileNumber = "9876543210",
                        isOtpVerified = true,
                        fullName = displayName ?: "Google User",
                        displayName = displayName?.lowercase()?.replace(" ", "_") ?: "google_user",
                        email = email ?: "google.user@gmail.com",
                        dateOfBirth = "1998-05-12",
                        gender = "Male",
                        selectedAvatarIndex = 4,
                        currentScreen = OnboardingScreen.ROLE_PRIMARY
                    )
                }
                onComplete(true, "Signed in successfully.")
            },
            onFailure = { errMsg ->
                _state.update {
                    it.copy(
                        isFirebaseAuthLoading = false,
                        firebaseAuthError = errMsg
                    )
                }
                onComplete(false, errMsg)
            }
        )
    }

    fun dismissSplash() {
        _state.update { it.copy(currentScreen = restoredScreen) }
    }

    fun resetToWelcome() {
        stopTimer()
        restoredScreen = OnboardingScreen.WELCOME
        _state.value = OnboardingState(currentScreen = OnboardingScreen.WELCOME)
    }

    fun navigateBack() {
        val current = _state.value.currentScreen
        val previous = when (current) {
            OnboardingScreen.SPLASH -> OnboardingScreen.SPLASH
            OnboardingScreen.WELCOME -> OnboardingScreen.WELCOME
            OnboardingScreen.MOBILE_AUTH -> OnboardingScreen.WELCOME
            OnboardingScreen.OTP_VERIFY -> OnboardingScreen.MOBILE_AUTH
            OnboardingScreen.EMAIL_AUTH -> OnboardingScreen.WELCOME
            OnboardingScreen.ROLE_PRIMARY -> {
                if (_state.value.mobileNumber.isNotEmpty() && _state.value.mobileNumber != "9876543210") {
                    OnboardingScreen.OTP_VERIFY
                } else if (_state.value.firebaseEmailInput.isNotEmpty()) {
                    OnboardingScreen.EMAIL_AUTH
                } else {
                    OnboardingScreen.WELCOME
                }
            }
            OnboardingScreen.ROLE_SECONDARY -> OnboardingScreen.ROLE_PRIMARY
            OnboardingScreen.LANGUAGES -> OnboardingScreen.ROLE_SECONDARY
            OnboardingScreen.GENRES -> OnboardingScreen.LANGUAGES
            OnboardingScreen.INSTRUMENTS -> OnboardingScreen.GENRES
            OnboardingScreen.LOCATION -> {
                if (canRolePlayInstruments(_state.value.primaryRole)) {
                    OnboardingScreen.INSTRUMENTS
                } else {
                    OnboardingScreen.GENRES
                }
            }
            OnboardingScreen.PROFILE_CREATION -> OnboardingScreen.LOCATION
            OnboardingScreen.SUCCESS -> OnboardingScreen.PROFILE_CREATION
            OnboardingScreen.DASHBOARD -> OnboardingScreen.SUCCESS
        }
        if (previous == OnboardingScreen.OTP_VERIFY) {
            // Restart resend timer if backing into OTP screen
            startTimer()
        } else {
            stopTimer()
        }
        _state.update { it.copy(currentScreen = previous) }
    }

    fun skipOnboardingToSuccessFast() {
        _state.update {
            it.copy(
                mobileNumber = "9876543210",
                isOtpVerified = true,
                primaryRole = "Singer / Vocalist",
                secondaryRole = "Lyricist",
                selectedLanguages = setOf("English", "Hindi (हिंदी)", "Punjabi (ਪੰਜਾਬੀ)"),
                selectedGenres = setOf("Bollywood / Filmi", "Indie Pop", "Sufi Devotional"),
                selectedInstruments = setOf("Acoustic Guitar", "Harmonium"),
                instrumentProficiency = mapOf(
                    "Acoustic Guitar" to SkillProficiency("3-5 Years", "Intermediate", "https://youtube.com/watch?v=demo1"),
                    "Harmonium" to SkillProficiency("5+ Years", "Advanced", "https://spotify.com/track/demo2")
                ),
                roleExperience = SkillProficiency("5-10 Years", "Advanced", "https://youtube.com/watch?v=vocals"),
                networkingLinks = mapOf(
                    "Spotify" to "https://open.spotify.com/artist/vedanshu",
                    "YouTube" to "https://youtube.com/c/vedanshumusic",
                    "SoundCloud" to "https://soundcloud.com/vedanshu-music",
                    "LinkedIn" to "https://linkedin.com/in/vedanshu",
                    "Newsletter" to "https://vedanshu.musiclinkd.com"
                ),
                state = "Maharashtra",
                city = "Mumbai",
                area = "Andheri West",
                fullName = "Vedanshu Srivastava",
                displayName = "vedanshu_music",
                bio = "Lead playback and crossover folk-pop vocalist. Specializing in classical Hindustani and acoustic indie tracks.",
                selectedAvatarIndex = 0,
                currentScreen = OnboardingScreen.SUCCESS
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
