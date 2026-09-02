package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class FundraisingProgress(
    val id: Long? = null,
    val created_at: String? = null,
    val ngo_id: String,
    val raised_amount: Double = 0.0,
    val goal_amount: Double = 0.0,
    val qr_content: String,
    val user_id: String
)
