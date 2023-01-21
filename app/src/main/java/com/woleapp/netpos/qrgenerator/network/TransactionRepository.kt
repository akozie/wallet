package com.woleapp.netpos.qrgenerator.network

import com.woleapp.netpos.qrgenerator.db.QrDao
import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.RegisterRequest
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutModel
import com.woleapp.netpos.qrgenerator.model.pay.PayModel
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TransactionRepository @Inject constructor(
    private val qrDao: QrDao
) {

    fun saveQrTransaction(qrTransactionResponseModel: QrTransactionResponseModel): Single<Long> =
        qrDao.insertQrTransaction(qrTransactionResponseModel)


}