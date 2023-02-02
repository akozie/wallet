package com.woleapp.netpos.qrgenerator.utils

import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.User

class Singletons {
    val gson = Gson()

    fun getCurrentlyLoggedInUser(): User? =
        gson.fromJson(Prefs.getString(PREF_USER, ""), User::class.java)

    fun getSavedQrModelRequest(): QrModelRequest? =
        gson.fromJson(Prefs.getString(PREF_GENERATE_QR, ""), QrModelRequest::class.java)
}