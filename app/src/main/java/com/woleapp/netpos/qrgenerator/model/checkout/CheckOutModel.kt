package com.woleapp.netpos.qrgenerator.model.checkout

import com.woleapp.netpos.qrgenerator.utils.RandomUtils
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.getGUID

data class CheckOutModel(
    val merchantId: String,
    val name: String,
    val email: String,
    val amount: Double,
    val currency: String,
    val orderId: String = getGUID()
)
