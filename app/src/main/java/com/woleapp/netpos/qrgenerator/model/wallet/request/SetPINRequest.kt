package com.woleapp.netpos.qrgenerator.model.wallet.request

data class SetPINRequest(
    val transaction_pin: String,
    val security_question: String,
    val security_answer: String
)