package com.woleapp.netpos.qrgenerator.model.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TallyWalletUserTransactionsResponseItem(
    val charges: Int,
    val created_at: String,
    val destination_acct: String,
    val destination_acct_name: String,
    val source_acct: String,
    val transaction_amount: Int,
    val transaction_id: String,
    val transaction_method: String,
    val receipt: String?
) : Parcelable
