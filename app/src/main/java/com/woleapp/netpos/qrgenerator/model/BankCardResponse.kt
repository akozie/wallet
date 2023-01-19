package com.woleapp.netpos.qrgenerator.model

data class BankCardResponse(
    val count: Int,
    val rows: List<RowX>
)