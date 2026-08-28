package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.UniFunderBottomBar
import com.example.ui.screens.SignInUpScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SelectNgoScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.SocialMediaScreen
import com.example.ui.screens.AskAiScreen
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.QrCodeScreen
import com.example.ui.theme.Malachite
import com.example.ui.theme.MyApplicationTheme

//There is a comment on line 36
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val vm: MainViewModel = viewModel()

                // Map system physical back button to our custom VM backstack
                BackHandler(enabled = vm.currentScreen != Screen.SignInUp) {
                    val handled = vm.navigateBack()
                    if (!handled) {
                        // If stack is at Home, let activity close naturally
                        finish()
                    }
                }

                // Bottom bar visibility conditions
                val showBottomBar = vm.isLoggedIn && (vm.currentScreen != Screen.AskAi)

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
                        if (showBottomBar) {
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
                                    contentDescription = "Ask AI",
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
                            Screen.SignInUp -> SignInUpScreen(vm = vm)
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
