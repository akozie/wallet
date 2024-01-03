package com.woleapp.netpos.qrgenerator.model.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchQrTokenResponseItem(
    val qr_code_id: String?,
    val qr_token: String?,
    val user_id: String?,
    val email: String?,
    val card_scheme: String?,
    val issuing_bank: String?,
    val date: String?
) : Parcelable