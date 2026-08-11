@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.onboarding.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.example.R
import kotlinx.coroutines.delay
import com.example.onboarding.models.OnboardingState
import com.example.onboarding.ui.components.M3InputField
import com.example.onboarding.ui.components.MusicNetworkArt
import com.example.onboarding.ui.components.errorShake
import com.example.ui.theme.*

// Premium Editorial Design Tokens for Light Neutral Theme
private val LightBg = Color(0xFFFCFCFD)
private val LightCard = Color(0xFFFFFFFF)
private val LightBorder = Color(0xFFECECF1)
private val LightTextPrimary = Color(0xFF181A22)
private val LightTextSecondary = Color(0xFF676B7B)

@Composable
fun SocialProofRow(count: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFF3F4F6), CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = count,
            fontWeight = FontWeight.ExtraBold,
            color = LightTextPrimary,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = LightTextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// SCREEN 1: WELCOME SCREEN (Modern 2026 Approach - Network & Ecosystem)
@Composable
fun WelcomeScreen(
    state: OnboardingState,
    onGoogleClick: () -> Unit,
    onMobileClick: () -> Unit,
    onEmailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGoogleSelector by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // 1. Header
            Column {
                Text(
                    text = "MusicLinkd",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        letterSpacing = (-1).sp,
                        fontSize = 28.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Professional Network For Music",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // 2. Hero Section
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Connect.\nCollaborate.\nCreate.",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        lineHeight = 52.sp,
                        letterSpacing = (-2).sp
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Build your profile, discover opportunities, connect with the music industry.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LightTextSecondary,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Social Proof
                Column {
                    SocialProofRow(count = "12,000+", label = "Musicians")
                    Spacer(modifier = Modifier.height(12.dp))
                    SocialProofRow(count = "3,500+", label = "Studios")
                    Spacer(modifier = Modifier.height(12.dp))
                    SocialProofRow(count = "800+", label = "Labels & Agencies")
                }
            }

            // 3. Actions & Footer
            Column {
                Button(
                    onClick = { showGoogleSelector = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("google_login_button")
                        .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightCard,
                        contentColor = LightTextPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GoogleIcon()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onMobileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("mobile_login_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightTextPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                ) {
                    Text(
                        text = "Continue with Mobile",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onEmailClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("email_login_button")
                        .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LightTextPrimary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = LightTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Email & Password",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "By continuing, you agree to Terms & Privacy",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LightTextSecondary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Google Select Account Modal
        if (showGoogleSelector) {
            AlertDialog(
                onDismissRequest = { showGoogleSelector = false },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showGoogleSelector = false }) {
                        Text("Cancel", color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = LightCard,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "G",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = BrandPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Choose an account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "to continue to MusicLinkd",
                            fontSize = 12.sp,
                            color = LightTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Account 1: Vedanshu
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGoogleSelector = false
                                    onGoogleClick()
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("V", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Vedanshu Srivastava",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = LightTextPrimary
                                )
                                Text(
                                    text = "vedanshu.srivastava@gmail.com",
                                    fontSize = 12.sp,
                                    color = LightTextSecondary
                                )
                            }
                        }

                        // Account 2: Guest Explorer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGoogleSelector = false
                                    onGoogleClick()
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF334155)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("M", color = Color(0xFFCBD5E1), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "MusicLinkd Explorer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = LightTextPrimary
                                )
                                Text(
                                    text = "guest.explorer@gmail.com",
                                    fontSize = 12.sp,
                                    color = LightTextSecondary
                                )
                            }
                        }

                        // Add another account
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showGoogleSelector = false
                                    onGoogleClick()
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3F3F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add account",
                                    tint = LightTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add another account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LightTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "To continue, Google will share your name, email address, language preference, and profile picture with MusicLinkd. See MusicLinkd's Privacy Policy and Terms of Service.",
                            fontSize = 10.sp,
                            color = LightTextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }
            )
        }
    }
}

// SCREEN 1.5: EMAIL AUTHENTICATION (Firebase Native Auth Integration)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailAuthScreen(
    state: OnboardingState,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    isLoginMode: Boolean,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Connection status & Token persistence banner
                val isFirebaseConnected = com.example.onboarding.database.FirebaseManager.isFullyConnected()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isFirebaseConnected) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isFirebaseConnected) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isFirebaseConnected) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFirebaseConnected) "Connected to Firebase Auth Gateway" else "Secure Offline Auth Mode Enabled",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFirebaseConnected) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔐 Encrypted SharedPreferences Session Token Active (Auto-Refresh Enabled)",
                        fontSize = 10.sp,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = if (isLoginMode) "Welcome back" else "Create your account",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        letterSpacing = (-1).sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isLoginMode) "Sign in with your email and password" else "Set up a secure professional login using Firebase",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LightTextSecondary,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email field
                Text(
                    text = "Email Address",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightTextPrimary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )
                OutlinedTextField(
                    value = emailValue,
                    onValueChange = onEmailChange,
                    placeholder = { Text("e.g. music@network.com", color = LightTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = LightBorder,
                        focusedTextColor = LightTextPrimary,
                        unfocusedTextColor = LightTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password field
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightTextPrimary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )
                OutlinedTextField(
                    value = passwordValue,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("At least 6 characters", color = LightTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_password_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        val iconImage = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(iconImage, contentDescription = "Toggle password visibility", tint = LightTextSecondary)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = LightBorder,
                        focusedTextColor = LightTextPrimary,
                        unfocusedTextColor = LightTextPrimary
                    )
                )

                // Account Linking Card
                var linkPhoneAccount by remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, LightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Auto-link with Phone/Google Auth",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightTextPrimary
                                )
                                Text(
                                    text = "Consolidate profile, studio bookings & messages into 1 account",
                                    fontSize = 10.sp,
                                    color = LightTextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = linkPhoneAccount,
                            onCheckedChange = { linkPhoneAccount = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandPrimary)
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Column {
                // Submit Button
                Button(
                    onClick = onSubmit,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightTextPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isLoginMode) "Log In" else "Create Account & Continue",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle register/login
                TextButton(
                    onClick = onToggleMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isLoginMode) "New to MusicLinkd? Create an account" else "Already have an account? Sign In",
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        com.example.onboarding.ui.components.LoadingOverlay(isLoading = isLoading)
    }
}

// SCREEN 2: MOBILE AUTHENTICATION (Pristine Light Redesign)
@Composable
fun MobileAuthScreen(
    mobileNumber: String,
    onNumberChange: (String) -> Unit,
    onSubmit: () -> Unit,
    error: String?,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome to MusicLinkd",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LightTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create your professional music identity",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LightTextSecondary,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Mobile Number",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LightTextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code prefix block
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(110.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(LightCard)
                            .border(1.dp, LightBorder, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🇮🇳 +91",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Text Input field
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = {
                            if (it.length <= 10) onNumberChange(it)
                        },
                        placeholder = {
                            Text(
                                text = "Enter 10-digit number",
                                color = LightTextSecondary,
                                fontSize = 15.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("mobile_input")
                            .errorShake(error != null),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        isError = error != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightCard,
                            unfocusedContainerColor = LightCard,
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = LightBorder,
                            errorBorderColor = Color.Red,
                            focusedTextColor = LightTextPrimary,
                            unfocusedTextColor = LightTextPrimary
                        )
                    )
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Security Note Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightCard),
                    border = BorderStroke(1.dp, LightBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "🔒",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "No spam & Instant Account Linking.",
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "We verify contact numbers to build mutual trust across Indian orchestras, labels, and studios. Automatically links with your Email/Google ID and persists JWT tokens securely via EncryptedSharedPreferences.",
                                color = LightTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Send OTP Action Button
            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("send_otp_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Send OTP",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}

// SCREEN 3: OTP VERIFICATION (Pristine Light Redesign)
@Composable
fun OtpVerificationScreen(
    mobileNumber: String,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    timerCount: Int,
    onResendClick: () -> Unit,
    onSubmit: () -> Unit,
    error: String?,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(6) { FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Verify Number",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LightTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "6-digit OTP code sent to +91 $mobileNumber ",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = LightTextSecondary,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "✏️",
                        modifier = Modifier
                            .clickable { onResendClick() }
                            .padding(4.dp),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 6 Boxes OTP Input Layout
                Row(
                    modifier = Modifier.fillMaxWidth().errorShake(error != null),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val characters = otpValue.padEnd(6, ' ')

                    for (i in 0 until 6) {
                        val char = characters[i]
                        val isFocused = otpValue.length == i

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.85f)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(LightCard)
                                .border(
                                    width = if (isFocused) 2.dp else 1.dp,
                                    color = if (isFocused) BrandPrimary else LightBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .focusRequester(focusRequesters[i]),
                            contentAlignment = Alignment.Center
                        ) {
                            if (i == 0) {
                                OutlinedTextField(
                                    value = otpValue,
                                    onValueChange = { newVal ->
                                        val cleaned = newVal.filter { it.isDigit() }.take(6)
                                        onOtpChange(cleaned)
                                        val currentLength = cleaned.length
                                        if (currentLength < 6) {
                                            focusRequesters[currentLength].requestFocus()
                                        } else {
                                            focusManager.clearFocus()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("otp_hidden_input")
                                        .opacity(0.01f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    singleLine = true
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { focusRequesters[0].requestFocus() }
                                )
                            }

                            Text(
                                text = if (char == ' ') "" else char.toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LightTextPrimary,
                                    fontSize = 22.sp
                                )
                            )
                        }
                    }
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 12.dp, start = 4.dp)
                    )
                } else {
                    Text(
                        text = "💡 Tip: Enter '123456' to pass immediately for verification.",
                        color = BrandSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 12.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Resend Timer layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timerCount > 0) {
                        Text(
                            text = "Resend OTP in ",
                            color = LightTextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "0:${timerCount.toString().padStart(2, '0')}",
                            color = BrandPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text = "Didn't receive the SMS? ",
                            color = LightTextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Resend Now",
                            color = BrandPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { onResendClick() }
                                .padding(4.dp)
                                .testTag("resend_button")
                        )
                    }
                }
            }

            // Continue validation CTA
            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("otp_continue_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}

// Custom opacity modifier helper
fun Modifier.opacity(value: Float) = this.then(
    Modifier.graphicsLayer(alpha = value)
)

@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.22f
        
        // Draw Red (Top segment)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )
        // Draw Yellow (Left segment)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 60f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )
        // Draw Green (Bottom segment)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 0f,
            sweepAngle = 120f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )
        // Draw Blue (Right segment & center bar)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 300f,
            sweepAngle = 60f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
        )
        // Center bar
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.4f),
            size = androidx.compose.ui.geometry.Size(w * 0.55f, stroke)
        )
    }
}

@Composable
fun SplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Automatically transition after 2.8 seconds
    LaunchedEffect(Unit) {
        delay(2800)
        onDismiss()
    }

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateIn = true
    }

    val scaleAnimate by animateFloatAsState(
        targetValue = if (animateIn) 1.0f else 0.92f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "SplashScale"
    )

    val alphaAnimate by animateFloatAsState(
        targetValue = if (animateIn) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 600, easing = LinearEasing),
        label = "SplashAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBg), // High-Contrast Editorial Soft Canvas (#FCFCFD)
        contentAlignment = Alignment.Center
    ) {
        // Soft Ambient Radial Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ColorPink.copy(alpha = 0.06f),
                            ColorSurface50,
                            ColorBg
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .graphicsLayer(
                    scaleX = scaleAnimate,
                    scaleY = scaleAnimate,
                    alpha = alphaAnimate
                )
                .semantics { contentDescription = "MusicLinkd Splash Branding Screen" }
        ) {
            // Main Branded Logo Container Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = ColorWhite,
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .widthIn(max = 380.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 36.dp, horizontal = 28.dp)
                ) {
                    // Logo Emblem Circle
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        ColorPink,
                                        ColorPink.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .border(3.dp, ColorWhite, CircleShape)
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_logo),
                            contentDescription = "MusicLinkd Official Brand Emblem",
                            tint = ColorWhite,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Brand Title
                    Text(
                        text = "MusicLinkd",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorText,
                        letterSpacing = (-1.2).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The Professional Network for Music Creators",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorText2,
                        letterSpacing = (-0.2).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Branded Feature Badges Row (using v4.0 design system chips)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge 1: Connect
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ColorSurface50,
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_connections),
                                    contentDescription = "Global Network",
                                    tint = ColorPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Connect",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorText
                                )
                            }
                        }

                        // Badge 2: Studios
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ColorSurface50,
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_studio),
                                    contentDescription = "Studio Booking",
                                    tint = ColorPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Studios",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorText
                                )
                            }
                        }

                        // Badge 3: Verified
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = ColorSurface50,
                            border = BorderStroke(1.dp, ColorBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_verified),
                                    contentDescription = "Verified Talent",
                                    tint = ColorGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Verified",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColorText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Loading Indicator in v4.0 Brand Pink
            CircularProgressIndicator(
                color = ColorPink,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
