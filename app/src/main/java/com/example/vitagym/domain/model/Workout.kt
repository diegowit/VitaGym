package com.example.vitagym.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val duration: Int,
    val date: Long = System.currentTimeMillis()
)
