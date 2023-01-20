package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutModel
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutResponse
import com.woleapp.netpos.qrgenerator.model.pay.PayModel
import com.woleapp.netpos.qrgenerator.model.pay.PayResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckoutService {

    @POST("v2/checkout")
    fun checkOut(
        @Body checkOutModel: CheckOutModel
    ): Single<CheckOutResponse>

    @POST("v2/pay")
    fun pay(
        @Body payModel: PayModel
    ): Single<PayResponse>

}