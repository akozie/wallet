package com.woleapp.netpos.qrgenerator.model

data class RegisterRequest(
    val email: String,
    val fullname: String,
    val mobile_phone: String,
    val password: String
)