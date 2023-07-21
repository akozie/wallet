package com.woleapp.netpos.qrgenerator.model.wallet

data class WalletUserResponse(
    val exists: Boolean,
    val info: Info
)
data class WalletStatusResponse(
    val exists: Boolean,
    val info: ArrayList<Any>
)