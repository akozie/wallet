package com.woleapp.netpos.qrgenerator.model.wallet.request

data class UpdateTransactionPinRequest(
    val new_pin: String,
    val otp: String,
    val security_answer: String,
    val security_question_id: String
)
