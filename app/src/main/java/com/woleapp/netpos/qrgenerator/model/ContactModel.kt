package com.woleapp.netpos.qrgenerator.model

data class ContactModel(
    val name: String,
    val phoneNumber: String,
    var selected: Boolean
)