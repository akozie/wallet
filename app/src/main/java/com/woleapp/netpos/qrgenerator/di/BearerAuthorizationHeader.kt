package com.woleapp.netpos.qrgenerator.di

import com.woleapp.netpos.qrgenerator.utils.Singletons
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BearerAuthInterceptor(apiToken: String) :
    Interceptor {
    private val xApiToken: String

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("X-API-KEY", xApiToken).build()
        return chain.proceed(authenticatedRequest)
    }

    init {
        xApiToken = apiToken
    }
}
