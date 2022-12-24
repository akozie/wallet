package com.woleapp.netpos.qrgenerator.ui.model

data class RegisterRequest(
    val email: String,
    val fullname: String,
    val mobile_phone: String,
    val password: String
)