package com.woleapp.netpos.qrgenerator.model.wallet.request

data class OtherBanksRequest(
    val beneficiaryAccountName: String,
    val beneficiaryAccountNumber: String,
    val beneficiaryBankCode: String,
    val beneficiaryBankName: String,
    val narration: String,
    val transactionAmount: String
)