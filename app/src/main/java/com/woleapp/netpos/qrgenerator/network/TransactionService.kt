package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.*
import io.reactivex.Single
import retrofit2.http.*

interface TransactionService {

    @GET("qrcode_transactions/{qr_code_id}")
     fun getEachTransaction(
        @Path("qr_code_id") qr_code_id:String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ) : Single<TransactionResponse>

}