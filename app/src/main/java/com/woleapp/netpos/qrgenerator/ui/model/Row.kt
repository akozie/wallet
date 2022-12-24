package com.woleapp.netpos.qrgenerator.ui.model

data class Row(
    val card_scheme: String,
    val createdAt: String,
    val id: Int,
    val logo_file: String,
    val updatedAt: String
){
    override fun toString(): String {
        return card_scheme
    }
}