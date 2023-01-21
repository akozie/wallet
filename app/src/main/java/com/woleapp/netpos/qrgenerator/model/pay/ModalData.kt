package com.woleapp.netpos.qrgenerator.model.pay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModalData(
    var status: Boolean,
    var amount: Double
) : Parcelable
