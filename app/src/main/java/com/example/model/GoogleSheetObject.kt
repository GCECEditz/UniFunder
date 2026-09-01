package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleSheetObject(
    val id: Long? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val name: String = "",
    val description: String? = ""
)
