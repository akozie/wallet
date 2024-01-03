package com.woleapp.netpos.qrgenerator.model.referrals

data class ConfirmReferralModel(
    val inviter_number: String,
    val invitee_number: String,
    val invitee_email: String
)