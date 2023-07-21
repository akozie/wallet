package com.woleapp.netpos.qrgenerator.model.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TallyWalletUserTransactionsResponse (
    val status: String,
    val message: String,
    val total_records:Int,
    val data: ArrayList<TallyWalletUserTransactionsResponseItem>
) : Parcelable