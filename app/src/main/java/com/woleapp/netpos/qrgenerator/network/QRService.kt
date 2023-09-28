package com.woleapp.netpos.qrgenerator.network

import com.google.gson.JsonObject
import com.woleapp.netpos.qrgenerator.model.*
import com.woleapp.netpos.qrgenerator.model.updatetoken.NewTokenRequest
import com.woleapp.netpos.qrgenerator.model.updatetoken.NewTokenResponse
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface QRService {

    @POST("auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @POST("auth/logout")
    fun logout(
        @Body logoutRequest: JsonObject?
    ): Single<GeneralResponse>

    @POST("auth/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Single<GeneralResponse>

    @POST("auth/refresh-token")
    fun getNewToken(
        @Body newTokenRequest: NewTokenRequest
    ): Single<NewTokenResponse>

    @POST("qr")
    fun generateQR(
        @Body generateQR: QrModelRequest
    ): Single<GenerateQRResponse>

    @GET("qr/cardschemes")
    fun getCardSchemes(
    ): Single<CardSchemeResponse>

    @GET("qr/banks")
    fun getCardBanks(
    ): Single<BankCardResponse>


    @GET("qr/fetch")
    fun fetchUserQrs(
    ): Single<FetchQrTokenResponse>


}