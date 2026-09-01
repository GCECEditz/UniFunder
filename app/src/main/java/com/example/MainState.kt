package com.example

import androidx.compose.ui.graphics.Color

enum class Screen {
    SignInUp,
    Home,
    SelectNgo,
    Profile,
    Feed,
    SocialMedia,
    AskAi,
    Budget,
    QrCode
}

data class Ngo(
    val id: String,
    val name: String,
    val website: String,
    val email: String,
    val description: String,
    val initials: String,
    val docTemplateUrl: String
)

data class Proposal(
    val id: String,
    val ngoId: String,
    val ngoName: String,
    val title: String,
    val content: String,
    val docsLink: String = "",
    var isSent: Boolean = false,
    var isApproved: Boolean = false
)

data class Budget(
    val id: String,
    val name: String,
    val info: String,
    val details: String,
    val items: List<String> = emptyList()
)



data class SocialDraft(
    val platform: String,
    val text: String,
    val isPosted: Boolean = false
)
