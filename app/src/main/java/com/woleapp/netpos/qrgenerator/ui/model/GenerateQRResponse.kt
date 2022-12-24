package com.woleapp.netpos.qrgenerator.ui.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.ParcelField
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "qr")
@Parcelize
data class GenerateQRResponse(
    val `data`: String,
    val qr_code_id : String,
    val success: Boolean
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true) var id: Int = 0

}