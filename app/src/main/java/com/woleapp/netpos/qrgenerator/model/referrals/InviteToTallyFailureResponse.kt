package com.woleapp.netpos.qrgenerator.model.referrals

data class InviteToTallyFailureResponse(
    val failures: List<String>?,
    val message: String,
    val status: String,
    val successful_invites: List<String?>?
)