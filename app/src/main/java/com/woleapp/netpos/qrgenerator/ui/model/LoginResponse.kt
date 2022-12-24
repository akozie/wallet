package com.woleapp.netpos.qrgenerator.ui.model

data class LoginResponse(
    val message: String,
    val success: Boolean,
    val token: String
)