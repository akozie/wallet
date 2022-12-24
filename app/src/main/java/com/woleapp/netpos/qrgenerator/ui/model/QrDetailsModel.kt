package com.woleapp.netpos.qrgenerator.ui.model

data class QrDetailsModel(
    val id:Int,
    val image:Int,
    val bankCard:String,
    val date:String,
    val transactions: Transactions
)
