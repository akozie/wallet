package com.woleapp.netpos.qrgenerator.model.wallet

data class GetSecurityQuestionResponseItem(
    val created_at: String,
    val id: Int,
    val question: String,
    val updated_at: String
){
    override fun toString(): String {
        return question
    }
}
data class NewGetSecurityQuestionResponseItem(
    val id: Int,
    val question: String,
){
    override fun toString(): String {
        return question
    }
}