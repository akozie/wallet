package com.woleapp.netpos.qrgenerator.model

data class LoginResponse(
    val message: String,
    val success: Boolean,
    val token: String
)