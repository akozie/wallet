package com.woleapp.netpos.qrgenerator.model.wallet

data class TallyWalletUserTransactionsResponseItem(
    val charges: String,
    val created_at: String,
    val destination_acct: String,
    val destination_acct_name: String,
    val destination_bank: String,
    val id: Int,
    val source_acct: String,
    val transaction_amount: String,
    val transaction_id: String,
    val transaction_method: String,
    val transaction_type: String,
    val updated_at: String,
    val user_id: Any
)