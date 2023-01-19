package com.woleapp.netpos.qrgenerator.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email")
    var email: String?=null,
    @SerializedName("exp")
    var exp: Int?=null,
    @SerializedName("fullname")
    var fullname: String?=null,
    @SerializedName("iat")
    var iat: Int?=null,
    @SerializedName("id")
    var id: Int?=null,
    @SerializedName("mobile_phone")
    var mobile_phone: String?=null
)