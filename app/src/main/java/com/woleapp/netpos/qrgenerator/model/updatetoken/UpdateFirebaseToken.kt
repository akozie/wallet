package com.woleapp.netpos.qrgenerator.model.updatetoken

data class UpdateFirebaseToken(
    val new_device_token: String,
    val old_device_token: String
)