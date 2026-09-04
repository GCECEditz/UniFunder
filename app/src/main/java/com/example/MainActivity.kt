package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.screens.UniFunderBottomBar
import com.example.screens.SignInUpScreen
import com.example.screens.HomeScreen
import com.example.screens.SelectNgoScreen
import com.example.screens.ProfileScreen
import com.example.screens.FeedScreen
import com.example.screens.SocialMediaScreen
import com.example.screens.AskAiScreen
import com.example.screens.BudgetScreen
import com.example.screens.QrCodeScreen
import com.example.ui.theme.Malachite
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val credentialManager = CredentialManager.create(this)

        setContent {
            MyApplicationTheme {
                val vm: MainViewModel = viewModel()
                val scope = rememberCoroutineScope()

                // Re-initialize credential if already logged in or when state changes
                LaunchedEffect(vm.isLoggedIn, vm.loggedInEmail) {
                    val email = vm.loggedInEmail.trim()
                    if (vm.isLoggedIn && email.isNotBlank()) {
                        Log.d("MainActivity", "Ensuring credentials for: [$email]")
                        val credential = GoogleAccountCredential.usingOAuth2(
                            this@MainActivity,
                            listOf(DriveScopes.DRIVE_METADATA_READONLY, "https://www.googleapis.com/auth/spreadsheets.readonly")
                        )
                        credential.selectedAccountName = email
                        vm.googleCredential = credential
                    }
                }

                val onGoogleSignInClick = {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID) 
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    scope.launch {
                        try {
                            Log.d("MainActivity", "Starting Credential Manager request...")
                            val result = credentialManager.getCredential(this@MainActivity, request)
                            val credential = result.credential
                            
                            Log.d("MainActivity", "Received credential type: ${credential.type}")
                            
                            val googleIdTokenCredential = try {
                                GoogleIdTokenCredential.createFrom(credential.data)
                            } catch (e: Exception) {
                                null
                            }

                            if (googleIdTokenCredential != null) {
                                val email = googleIdTokenCredential.id
                                val displayName = googleIdTokenCredential.displayName ?: "User"
                                
                                Log.d("MainActivity", "Google Sign-In successful for: $email")
                                
                                vm.loggedInDisplayName = displayName
                                vm.handleGoogleSignIn(email)
                                
                                // Initialize Drive & Sheets credential
                                val driveCredential = GoogleAccountCredential.usingOAuth2(
                                    this@MainActivity,
                                    listOf(DriveScopes.DRIVE_METADATA_READONLY, "https://www.googleapis.com/auth/spreadsheets.readonly")
                                )
                                driveCredential.selectedAccountName = email
                                vm.googleCredential = driveCredential
                            } else {
                                Log.w("MainActivity", "Failed to parse GoogleIdTokenCredential from ${credential.type}")
                                vm.authError = "Unexpected login response type: ${credential.type}"
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Credential Manager failed", e)
                            vm.authError = "Sign-In failed: ${e.localizedMessage}"
                        }
                    }
                }

                // Map system physical back button to our custom VM backstack
                BackHandler(enabled = vm.currentScreen != Screen.SignInUp) {
                    val handled = vm.navigateBack()
                    if (!handled) {
                        // If stack is at Home, let activity close naturally
                        finish()
                    }
                }

                // Bottom bar visibility conditions
                val showBottomBar = vm.isLoggedIn && (vm.currentScreen != Screen.SignInUp) && (vm.currentScreen != Screen.AskAi)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            UniFunderBottomBar(
                                currentScreen = vm.currentScreen,
                                onNavigate = { screen -> vm.navigateTo(screen) },
                            )
                        }
                    },
                    floatingActionButton = {
                        //vm.currentScreen != Screen.Budget to avoid double chat buttons
                        if (showBottomBar && vm.currentScreen != Screen.Budget && vm.currentScreen != Screen.SignInUp) {
                            FloatingActionButton(
                                onClick = { vm.navigateTo(Screen.AskAi) },
                                containerColor = Malachite,
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier
                                    .padding(bottom = 12.dp, end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChatBubble,
                                    contentDescription = stringResource(R.string.content_desc_askAI),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Crossfade(
                        targetState = vm.currentScreen,
                        modifier = Modifier.padding(innerPadding),
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            Screen.SignInUp -> SignInUpScreen(
                                vm = vm,
                                onGoogleSignInClick = { onGoogleSignInClick() }
                            )
                            Screen.Home -> HomeScreen(vm = vm)
                            Screen.SelectNgo -> SelectNgoScreen(vm = vm)
                            Screen.Profile -> ProfileScreen(vm = vm)
                            Screen.Feed -> FeedScreen(vm = vm)
                            Screen.SocialMedia -> SocialMediaScreen(vm = vm)
                            Screen.AskAi -> AskAiScreen(vm = vm)
                            Screen.Budget -> BudgetScreen(vm = vm)
                            Screen.QrCode -> QrCodeScreen(vm = vm)
                        }
                    }
                }
            }
        }
    }
}
