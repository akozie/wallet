package com.woleapp.netpos.qrgenerator.model.wallet

data class GetSecurityQuestionResponseItem(
    val created_at: String,
    val id: Int,
    val questions: String,
    val updated_at: String
)