package com.woleapp.netpos.qrgenerator.model

import com.woleapp.netpos.qrgenerator.utils.UtilityParam

data class QrModelRequest(
    val user_id: Int?=null,
    val card_cvv: String,
    val card_expiry: String,
    val card_number: String,
    val card_scheme: String,
    val email: String?,
    val fullname: String?,
    val issuing_bank: String,
    val mobile_phone: String?,
    val app_code: String?=UtilityParam.STRING_TALLY_CONSTANT
  //  val date_created: String?,
)
