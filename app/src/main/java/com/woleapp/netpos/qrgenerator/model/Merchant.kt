package com.woleapp.netpos.qrgenerator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Merchant(
    val account_name: @RawValue Any?,
    val account_number: String?,
    val address: String?,
    val bank_code: @RawValue Any?,
    val bank_type: @RawValue Any?,
    val business_occupation_code: @RawValue Any?,
    val contact_name: String?,
    val contact_title: String?,
    val createdAt: String?,
    val email: String?,
    val email_alert: @RawValue Any?,
    val id: Int?,
    val lga_lcda: @RawValue Any?,
    val mastercard_acquired_number: @RawValue Any?,
    val merchant_category_code: @RawValue Any?,
    val mobile_phone: String?,
    val partner_id: String?,
    val postal_code: @RawValue Any?,
    val ptsp: @RawValue Any?,
    val slip_footer: @RawValue Any?,
    val slip_header: String?,
    val state_code: @RawValue Any?,
    val terminal_model_code: @RawValue Any?,
    val terminal_owner_code: @RawValue Any?,
    val updatedAt: String?,
    val url: @RawValue Any?,
    val verve_acquired_number: @RawValue Any?,
    val visa_acquired_number: @RawValue Any?
) : Parcelable