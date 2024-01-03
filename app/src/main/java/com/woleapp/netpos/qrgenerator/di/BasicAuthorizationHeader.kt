package com.woleapp.netpos.qrgenerator.di

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor(user: String, password: String) :

    Interceptor {
    private val credentials: String

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

    init {
        credentials = Credentials.basic(user, password)
    }
}

class HeaderInterceptor(private val headerName: String, private val headerValue: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val modifiedRequest = originalRequest.newBuilder()
            .header(headerName, headerValue)
            .build()

        return chain.proceed(modifiedRequest)
    }
}

