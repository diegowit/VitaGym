package com.example.vitagym.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val duration: Int = 0,
    val date: Long = System.currentTimeMillis()
)
