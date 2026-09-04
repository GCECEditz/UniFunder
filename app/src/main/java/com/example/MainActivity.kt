package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import android.accounts.AccountManager
import com.google.android.gms.common.AccountPicker
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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

        // Removed CredentialManager in favor of AccountPicker

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

                val accountPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                        if (accountName != null) {
                            Log.d("MainActivity", "Account selected: $accountName")
                            vm.loggedInDisplayName = accountName.substringBefore("@")
                            vm.handleGoogleSignIn(accountName)

                            // Initialize Drive & Sheets credential
                            val driveCredential = GoogleAccountCredential.usingOAuth2(
                                this@MainActivity,
                                listOf(DriveScopes.DRIVE_METADATA_READONLY, "https://www.googleapis.com/auth/spreadsheets.readonly")
                            )
                            driveCredential.selectedAccountName = accountName
                            vm.googleCredential = driveCredential
                        }
                    } else {
                        Log.w("MainActivity", "Account picker cancelled or failed")
                    }
                }

                val onGoogleSignInClick = {
                    val intent = AccountPicker.newChooseAccountIntent(
                        AccountPicker.AccountChooserOptions.Builder()
                            .setAllowableAccountsTypes(listOf("com.google"))
                            .build()
                    )
                    accountPickerLauncher.launch(intent)
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
