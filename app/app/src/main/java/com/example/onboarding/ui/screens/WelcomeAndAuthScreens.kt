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
        Spacer(modifier = Modifier.width(Dimens.sp2))
        Text(
            text = count,
            fontWeight = FontWeight.ExtraBold,
            color = LightTextPrimary,
            style = Typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = LightTextSecondary,
            style = Typography.bodyLarge
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
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        letterSpacing = (-1).sp,
                        fontSize = 28.sp
                    )
                )
                Spacer(modifier = Modifier.height(Dimens.sp1))
                Text(
                    text = "Professional Network For Music",
                    style = Typography.bodyLarge.copy(
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
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        lineHeight = 52.sp,
                        letterSpacing = (-2).sp
                    )
                )
                
                Spacer(modifier = Modifier.height(Dimens.sp2))
                
                Text(
                    text = "Build your profile, discover opportunities, connect with the music industry.",
                    style = Typography.bodyLarge.copy(
                        color = LightTextSecondary,
                        style = Typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Social Proof
                Column {
                    SocialProofRow(count = "12,000+", label = "Musicians")
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    SocialProofRow(count = "3,500+", label = "Studios")
                    Spacer(modifier = Modifier.height(Dimens.sp2))
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
                        .border(1.dp, LightBorder, RoundedCornerShape(Dimens.r2)),
                    shape = RoundedCornerShape(Dimens.r2),
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
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Text(
                            text = "Continue with Google",
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))

                Button(
                    onClick = onMobileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("mobile_login_button"),
                    shape = RoundedCornerShape(Dimens.r2),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightTextPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Text(
                            text = "Continue with Mobile",
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))

                OutlinedButton(
                    onClick = onEmailClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("email_login_button")
                        .border(1.dp, LightBorder, RoundedCornerShape(Dimens.r2)),
                    shape = RoundedCornerShape(Dimens.r2),
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
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Text(
                            text = "Continue with Email & Password",
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp3))

                Text(
                    text = "By continuing, you agree to Terms & Privacy",
                    style = Typography.bodyLarge.copy(
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
                shape = RoundedCornerShape(Dimens.r3),
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
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Text(
                            text = "Choose an account",
                            style = Typography.bodyLarge,
                            color = LightTextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "to continue to MusicLinkd",
                            style = Typography.labelLarge,
                            color = LightTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(Dimens.sp1))

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
                                Text("V", color = Color.White, style = Typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.width(Dimens.sp2))
                            Column {
                                Text(
                                    text = "Vedanshu Srivastava",
                                    style = Typography.bodyLarge,
                                    color = LightTextPrimary
                                )
                                Text(
                                    text = "vedanshu.srivastava@gmail.com",
                                    style = Typography.labelLarge,
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
                                Text("M", color = Color(0xFFCBD5E1), style = Typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.width(Dimens.sp2))
                            Column {
                                Text(
                                    text = "MusicLinkd Explorer",
                                    style = Typography.bodyLarge,
                                    color = LightTextPrimary
                                )
                                Text(
                                    text = "guest.explorer@gmail.com",
                                    style = Typography.labelLarge,
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
                            Spacer(modifier = Modifier.width(Dimens.sp2))
                            Text(
                                text = "Add another account",
                                style = Typography.bodyLarge,
                                color = LightTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(Dimens.sp1))
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
                        .clip(RoundedCornerShape(Dimens.r2))
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
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text(
                            text = if (isFirebaseConnected) "Connected to Firebase Auth Gateway" else "Secure Offline Auth Mode Enabled",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFirebaseConnected) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Text(
                        text = "🔐 Encrypted SharedPreferences Session Token Active (Auto-Refresh Enabled)",
                        fontSize = 10.sp,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.sp3))

                // Title
                Text(
                    text = if (isLoginMode) "Welcome back" else "Create your account",
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = LightTextPrimary,
                        letterSpacing = (-1).sp
                    )
                )
                Spacer(modifier = Modifier.height(Dimens.sp1))
                Text(
                    text = if (isLoginMode) "Sign in with your email and password" else "Set up a secure professional login using Firebase",
                    style = Typography.bodyLarge.copy(
                        color = LightTextSecondary,
                        style = Typography.bodyLarge
                    )
                )

                Spacer(modifier = Modifier.height(Dimens.sp3))

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
                    shape = RoundedCornerShape(Dimens.r2),
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
                    shape = RoundedCornerShape(Dimens.r2),
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
                Spacer(modifier = Modifier.height(Dimens.sp2))
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
                                    style = Typography.bodyLarge,
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
                    Spacer(modifier = Modifier.height(Dimens.sp2))
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
                    shape = RoundedCornerShape(Dimens.r2),
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
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))

                // Toggle register/login
                TextButton(
                    onClick = onToggleMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isLoginMode) "New to MusicLinkd? Create an account" else "Already have an account? Sign In",
                        color = BrandPrimary,
                        style = Typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.sp1))
            }
        }
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
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = LightTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create your professional music identity",
                    style = Typography.bodyLarge.copy(
                        color = LightTextSecondary,
                        style = Typography.bodyLarge
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Mobile Number",
                    style = Typography.bodyLarge.copy(
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
                            style = Typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary,
                                style = Typography.bodyLarge
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.sp2))

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
                                style = Typography.bodyLarge
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
                        style = Typography.labelLarge,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Security Note Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.r2),
                    colors = CardDefaults.cardColors(containerColor = LightCard),
                    border = BorderStroke(1.dp, LightBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.sp2),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "🔒",
                            style = Typography.bodyLarge,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "No spam & Instant Account Linking.",
                                fontWeight = FontWeight.Bold,
                                color = LightTextPrimary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(Dimens.sp1))
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
                        style = Typography.bodyLarge.copy(
                            style = Typography.bodyLarge
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
                    style = Typography.bodyLarge.copy(
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
                        style = Typography.bodyLarge.copy(
                            color = LightTextSecondary,
                            style = Typography.labelLarge
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
                                style = Typography.bodyLarge.copy(
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
                        style = Typography.labelLarge,
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
                            style = Typography.labelLarge
                        )
                        Text(
                            text = "0:${timerCount.toString().padStart(2, '0')}",
                            color = BrandPrimary,
                            style = Typography.bodyLarge
                        )
                    } else {
                        Text(
                            text = "Didn't receive the SMS? ",
                            color = LightTextSecondary,
                            style = Typography.labelLarge
                        )
                        Text(
                            text = "Resend Now",
                            color = BrandPrimary,
                            style = Typography.bodyLarge,
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
                        style = Typography.bodyLarge.copy(
                            style = Typography.bodyLarge
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
        targetValue = if (animateIn) 1.05f else 0.95f,
        animationSpec = tween(durationMillis = 2800, easing = LinearOutSlowInEasing),
        label = "SplashScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09071B)), // Deep Night Canvas
        contentAlignment = Alignment.Center
    ) {
        // High-Resolution Atmospheric Studio Image with Gradient Scrim
        coil.compose.AsyncImage(
            model = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=1200&auto=format&fit=crop&q=80",
            contentDescription = "Music Production Studio background photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = scaleAnimate, scaleY = scaleAnimate)
                .alpha(0.38f)
        )

        // Dark Radial & Vertical Gradient Overlays for High Contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF09071B).copy(alpha = 0.85f),
                            Color(0xFF09071B).copy(alpha = 0.40f),
                            Color(0xFF09071B).copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .semantics { contentDescription = "MusicLinkd Splash Branding Screen" }
        ) {
            // Glassmorphic Branded Logo Container
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFEA4C89).copy(alpha = 0.85f),
                                Color(0xFF7C3AED).copy(alpha = 0.65f)
                            )
                        )
                    )
                    .border(2.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                // Branded Logo Drawable
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_logo),
                    contentDescription = "MusicLinkd Official Brand Emblem",
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Title
            Text(
                text = "MusicLinkd",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-1.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "The Professional Network for Music Creators",
                style = Typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = (-0.3).sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.sp3))

            // Branded Feature Badges Row (using branded drawables: connections, studio, verified)
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(Dimens.r3),
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_connections),
                            contentDescription = "Global Network",
                            tint = Color(0xFFEA4C89),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(Dimens.r3),
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_studio),
                            contentDescription = "Studio Booking",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Studios", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(Dimens.r3),
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_verified),
                            contentDescription = "Verified Talent",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp4))

            // Loading Indicator
            CircularProgressIndicator(
                color = Color(0xFFEA4C89),
                strokeWidth = 3.dp,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
