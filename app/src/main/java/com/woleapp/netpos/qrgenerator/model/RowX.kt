package com.woleapp.netpos.qrgenerator.model

data class RowX(
    val bank_id: String,
    val bank_name: String,
    val createdAt: String,
    val id: Int,
    val logo_file: String,
    val theme_colour: String,
    val updatedAt: String
){
    override fun toString(): String {
        return bank_name
    }
}