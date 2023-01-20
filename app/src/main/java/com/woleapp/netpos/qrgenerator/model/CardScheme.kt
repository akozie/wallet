package com.woleapp.netpos.qrgenerator.model

enum class CardScheme(val type: String) {
    MASTERCARD(type = "MasterCard"),
    VISA(type = "Visa"),
    VERVE(type = "Verve")
}