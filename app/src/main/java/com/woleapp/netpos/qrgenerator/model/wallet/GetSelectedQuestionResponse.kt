package com.woleapp.netpos.qrgenerator.model.wallet

data class GetSelectedQuestionResponse(
    val created_at: String,
    val id: Int,
    val question_id: Int,
    val question: String,
    val updated_at: String,
    val user_id: String
)
