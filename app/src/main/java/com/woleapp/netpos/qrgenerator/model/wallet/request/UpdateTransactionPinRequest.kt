package com.woleapp.netpos.qrgenerator.model.wallet.request

data class UpdateTransactionPinRequest(
    val old_pin: String,
    val new_pin: String,
    val otp: String,
    val security_answer: String,
    val security_question_id: String,
    val adminAccessToken: String,
    val userTokenId: String,
    val accountId: String
)
