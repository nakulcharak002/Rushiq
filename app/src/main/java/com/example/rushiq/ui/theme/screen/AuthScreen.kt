package com.example.rushiq.ui.theme.screen

import com.example.rushiq.R
import android.app.Activity
import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rushiq.ui.theme.utils.AuthState
import com.example.rushiq.ui.theme.viewmodels.AuthViewModel
import io.ktor.websocket.Frame

enum class RushiqAuthMode {
    SIGN_IN,
    SIGN_UP,
}


@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val authState by viewModel.authState.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    var authMode by remember { mutableStateOf(RushiqAuthMode.SIGN_IN) }
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var phoneNumber by remember { mutableStateOf(value = "") }

    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Define colors
    val rushiqPurple = Color(color = 0xFF5D0079)
    val rushiqPink = Color(color = 0xFFFF4D80)
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(
            Color(color = 0xFF38004F), // Deep purple
            Color(color = 0xFF6C0080), // Rich purple
        ),
    )

    val textFieldBackground = Color(color = 0xFF8F8F8F)

    // Google Sign In Result Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        Log.d("AuthScreen", "Google Sign-In result received: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("AuthScreen", "Google sign-In successful , handling result")
            viewModel.handleGoogleSignInResult(result.data)
        } else {
            Log.d("AuthScreen ", "Google sign-In cancelled or failed")
            viewModel.resetAuthState()
            if (result.resultCode != Activity.RESULT_CANCELED) {
                viewModel.setErrorState("Google sig-in failed")
            }
        }
    }

    LaunchedEffect(authState) {
        Log.d("AuthScreen", " AuthState :$authState")
    }

    LaunchedEffect(isAuthenticated) {
        Log.d("AuthScreen", "Auth state changed : isAuthentication = $isAuthenticated")
        if (isAuthenticated) {
            Log.d("AuthScreen", "Authentication successful , calling onAuthScreen")
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo and Header
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "RushiQ",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = rushiqPink,
            )

            // Tagline
            Text(
                text = "Groceries\ndelivered  in\n10 minutes",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = textFieldBackground,
                    unfocusedContainerColor = textFieldBackground,
                    focusedBorderColor = rushiqPurple,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = rushiqPink,
                ),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                          if (passwordVisible) {
                                painterResource(id = R.drawable.eye_open_svgerepo_com)
                            } else {
                                painterResource(id = R.drawable.eye_closed_svgerepo_com)
                            },
                            contentDescription = if (passwordVisible) "Hide password" else "Show Password",
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (email.isNotBlank() && password.isNotEmpty()) {
                            if (authMode == RushiqAuthMode.SIGN_IN) {
                                viewModel.signInWithCustomEmail(email, password)
                            } else {
                                viewModel.signUpWithCustomEmail(email, password)
                            }
                        }
                    },
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = textFieldBackground,
                    unfocusedContainerColor = textFieldBackground,
                    focusedBorderColor = rushiqPurple,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = rushiqPink,
                ),
                shape = RoundedCornerShape(8.dp),
            )

            if (authMode == RushiqAuthMode.SIGN_UP) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Text(
                            "+91",
                            color = rushiqPink,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = textFieldBackground,
                        unfocusedContainerColor = textFieldBackground,
                        focusedBorderColor = rushiqPurple,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = rushiqPink,
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In/Sign Up Button
            Button(
                onClick = {
                    if (authMode == RushiqAuthMode.SIGN_IN) {
                        viewModel.signInWithCustomEmail(email, password)
                    } else {
                        viewModel.signUpWithCustomEmail(email, password)
                        // Force an immediate auth status check
                        viewModel.checkAuthStatus()
                    }
                },
                enabled = email.isNotBlank() && password.isNotBlank() && authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = rushiqPink,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = if (authMode == RushiqAuthMode.SIGN_IN) "Sign In" else "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Google Sign In Button
            Button(
                onClick = {
                    if (activity != null) {
                        try {
                            val signInIntent = viewModel.getGoogleSignInIntent(activity)
                            googleSignInLauncher.launch(signInIntent)
                        } catch (e: Exception) {
                            Log.e("AuthScreen", "Error launching Google Sign-In: ${e.message}")
                            viewModel.setErrorState("Failed to launch Google sign-in: ${e.message}")
                        }
                    } else {
                        viewModel.setErrorState("Cannot access activity")
                    }
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Continue with Google",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle between sign in and sign up
            TextButton(
                onClick = {
                    authMode = if (authMode == RushiqAuthMode.SIGN_IN) {
                        RushiqAuthMode.SIGN_UP
                    } else {
                        RushiqAuthMode.SIGN_IN
                    }
                },
            ) {
                Text(
                    if (authMode == RushiqAuthMode.SIGN_IN) {
                        "Don't have an account? Sign Up"
                    } else {
                        "Already have an account? Sign In"
                    },
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status messages
            when (authState) {
                is AuthState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(color = 0x33FF0000),
                        ),
                    ) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                        )
                    }
                }

                is AuthState.Success -> {
                    if (isAuthenticated) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(color = 0x3300FF00),
                            ),
                        ) {
                            Text(
                                text = (authState as AuthState.Success).message,
                                modifier = Modifier.padding(16.dp),
                                color = Color.White,
                            )
                        }
                    }
                }

                else -> { /* Nothing to show for other states */ }
            }

            // Terms and Privacy at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "By continuing , you agree to our",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "Terms of Use",
                            color = rushiqPink.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        Text(
                            " & ",
                            color = rushiqPink.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                        Text(
                            "Privacy Policy",
                            color = rushiqPink.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}