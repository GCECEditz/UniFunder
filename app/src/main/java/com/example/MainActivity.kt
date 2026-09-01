package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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
import androidx.compose.ui.platform.testTag
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

//There is a nice comment on line 36
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            MyApplicationTheme {
                val vm: MainViewModel = viewModel()

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        account?.let { acc ->
                            vm.loggedInDisplayName = acc.displayName ?: acc.email?.substringBefore("@") ?: "User"
                            acc.email?.let { email ->
                                vm.handleGoogleSignIn(email)
                            } ?: run {
                                vm.authError = "Google Account does not have an email address associated."
                            }
                        }
                    } catch (e: ApiException) {
                        Log.e("MainActivity", "Google sign in failed code=${e.statusCode}", e)
                        vm.authError = "Google Sign-In failed (Error ${e.statusCode}). Check SHA-1 registration in Google Console."
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Unexpected error during sign in", e)
                        vm.authError = "An unexpected error occurred: ${e.message}"
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
                                    .testTag("floating_ask_ai_button")
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
                                onGoogleSignInClick = {
                                    launcher.launch(googleSignInClient.signInIntent)
                                },
                                googleSignInClient = googleSignInClient
                            )
                            Screen.Home -> HomeScreen(vm = vm)
                            Screen.SelectNgo -> SelectNgoScreen(vm = vm)
                            Screen.Profile -> ProfileScreen(vm = vm, googleSignInClient = googleSignInClient)
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
