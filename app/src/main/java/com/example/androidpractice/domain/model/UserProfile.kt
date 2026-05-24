package com.example.androidpractice.domain.model

data class UserProfile(
    val fullName: String = "",
    val position: String = "",
    val photoUri: String = "",
    val resumeUrl: String = "",
    val resumeLocalUri: String = ""  // URI локального файла резюме на устройстве
)
