package com.woleapp.netpos.qrgenerator.model

data class AllMerchantResponse(
    val `data`: List<Merchant>,
    val status: Boolean
)