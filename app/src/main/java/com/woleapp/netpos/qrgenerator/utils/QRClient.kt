package com.woleapp.netpos.qrgenerator.utils

import com.woleapp.netpos.qrgenerator.di.BasicAuthInterceptor
import com.woleapp.netpos.qrgenerator.network.QRService
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class QRClient {
    companion object {

        private fun getBaseOkhttpClientBuilder(): OkHttpClient.Builder {
            val okHttpClientBuilder = OkHttpClient.Builder()

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(loggingInterceptor)

            return okHttpClientBuilder
        }

        private fun getOkHttpClient() =
            getBaseOkhttpClientBuilder()
                .addInterceptor(
                    BasicAuthInterceptor(
                        UtilityParam.STRING_AUTH_USER_NAME,
                        UtilityParam.STRING_AUTH_PASSWORD
                    )
                )
                .build()

        private val BASE_URL = UtilityParam.STRING_TALLY_BASE_URL
        private var INSTANCE: QRService? = null
        fun getInstance(): QRService = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QRService::class.java)
                .also {
                    INSTANCE = it
                }
        }
    }
}


//class TokenInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        var request = chain.request()
//        val headersInReq = request.headers
//        if (headersInReq["Authorization"].isNullOrEmpty()) {
//            val credentials = "${UtilityParam.STRING_AUTH_USER_NAME}:${UtilityParam.STRING_AUTH_PASSWORD}" // Replace with your actual username and password
//            val credentialsBase64 = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
//            val authHeaderValue = "Basic $credentialsBase64"
//
//            request = request.newBuilder().addHeader("Authorization", authHeaderValue).build()
//        }
//        val response = chain.proceed(request)
//        val body = response.body
//        val bodyString = body?.string()
//        return response.newBuilder().body(bodyString!!.toResponseBody(body.contentType()))
//            .build()
//    }
//}


