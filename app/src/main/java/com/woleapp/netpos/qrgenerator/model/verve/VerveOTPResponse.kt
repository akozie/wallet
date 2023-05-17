package com.woleapp.netpos.qrgenerator.model.verve

data class VerveOTPResponse(
    val amount: String,
    val code: String,
    val message: String,
    val orderId: String,
    val result: String,
    val status: String,
    val transId: String
)