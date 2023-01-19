package com.woleapp.netpos.qrgenerator.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenerateQRResponse(
    val qr_code_id : String?,
    val `data`: String?,
    val success: Boolean,
    val card_scheme: String?,
    val issuing_bank: String?,
    val date:String?=null
) : Parcelable

@Entity(tableName = "qr")
@Parcelize
data class DomainQREntity(
    @PrimaryKey
    val qr_code_id : String,
    val userId:String,
    val qrData:GenerateQRResponse
) : Parcelable