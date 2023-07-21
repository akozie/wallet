package com.woleapp.netpos.qrgenerator.model.wallet

data class Info(
    val available_balance: Int,
    val email: String,
    val fullname: String,
    val message: String,
    val pending_balance: Int,
    val phone_no: String,
    val pin: Boolean,
    val status: String,
    val verified: Boolean
)