package com.woleapp.netpos.qrgenerator.model

data class LoginResponse(
    val message: String,
    val success: Boolean,
    val token: String,
    val refreshToken: String
)

data class WalletLoginResponse(
    val status: String,
    val data: LoginData
)

data class LoginData(
    val adminAccessToken: String,
    val userTokenId: String,
    val accountId: String,
)