package com.woleapp.netpos.qrgenerator.model.updatetoken

data class NewTokenRequest(
    val email: String,
    val refresh_token: String
)