package com.woleapp.netpos.qrgenerator.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


data class FrequentBeneficiariesModel(
    val image: String,
    val user_name : String,
)
