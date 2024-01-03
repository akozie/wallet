package com.woleapp.netpos.qrgenerator.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class WalletLoginRequest(
    val username: String,
    val password: String
)