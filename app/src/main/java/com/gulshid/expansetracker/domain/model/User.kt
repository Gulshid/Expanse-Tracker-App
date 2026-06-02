package com.gulshid.expansetracker.domain.model


data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null
)