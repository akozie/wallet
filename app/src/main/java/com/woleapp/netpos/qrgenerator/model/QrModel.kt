package com.woleapp.netpos.qrgenerator.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class QrModel(
    val `data`: String,
    val qr_code_id : String,
    val card_scheme: String,
    val date: String,
    val issuing_bank: String,
) : Parcelable
