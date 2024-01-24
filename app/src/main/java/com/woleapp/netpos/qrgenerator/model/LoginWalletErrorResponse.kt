package com.woleapp.netpos.qrgenerator.model

data class LoginWalletErrorResponse(
    val error: String,
    val message: String,
    val status: String,
    val success: Boolean
)