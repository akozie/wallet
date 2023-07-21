package com.woleapp.netpos.qrgenerator.model.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchQrTokenResponse(
    val `data`: List<FetchQrTokenResponseItem>,
    val status: String
) : Parcelable