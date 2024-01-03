package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.AllMerchantResponse
import com.woleapp.netpos.qrgenerator.model.MerchantResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MerchantService {
    @GET("user/search-partner-user")
    fun getMerchant(
        @Header("token") token: String,
        @Query("search") search: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ) : Single<MerchantResponse>

    @GET("user/get-partner-user")
    fun getAllMerchant(
        @Header("token") token: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ) : Single<AllMerchantResponse>

}