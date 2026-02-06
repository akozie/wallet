package com.woleapp.netpos.qrgenerator.network

import com.google.gson.JsonObject
import com.woleapp.netpos.qrgenerator.model.*
import com.woleapp.netpos.qrgenerator.model.qrinfo.StoreQrInfoRequest
import com.woleapp.netpos.qrgenerator.model.referrals.ConfirmReferralModel
import com.woleapp.netpos.qrgenerator.model.referrals.InviteToTallyFailureResponse
import com.woleapp.netpos.qrgenerator.model.referrals.InviteToTallyModel
import com.woleapp.netpos.qrgenerator.model.updatetoken.StoreDeviceToken
import com.woleapp.netpos.qrgenerator.model.updatetoken.UpdateFirebaseToken
import com.woleapp.netpos.qrgenerator.model.wallet.*
import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface UpdateTokenService {

    @POST("storeDeviceToken")
    fun storeDeviceToken(
        @Header("Authorization") token: String,
        @Body storeDeviceToken: StoreDeviceToken
    ): Single<GeneralWalletResponse>


    @PUT("updateDeviceToken")
    fun updateFirebaseToken(
        @Header("Authorization") token: String,
        @Header("X-API-KEY") xApiToken: String,
        @Body updateFirebaseToken: UpdateFirebaseToken
    ): Single<GeneralWalletResponse>


}