package com.woleapp.netpos.qrgenerator.network

import android.content.Context
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.di.BasicAuthInterceptor
import com.woleapp.netpos.qrgenerator.network.QRService
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.USER_TOKEN
import com.woleapp.netpos.qrgenerator.utils.UtilityParam
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class UpdateTokenClient {
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
                    TokenInterceptor()
                )
                .build()

        private val BASE_URL = UtilityParam.STRING_FIREBASE_TOKEN
        private var INSTANCE: UpdateTokenService? = null
        fun getInstance(): UpdateTokenService = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UpdateTokenService::class.java)
                .also {
                    INSTANCE = it
                }
        }
    }
}


class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val headersInReq = request.headers
        if (headersInReq["Authorization"].isNullOrEmpty()) {
            Prefs.getString(USER_TOKEN, null)?.let {
                request = request.newBuilder().addHeader("Authorization", "Bearer $it")
                    .addHeader("X-API-KEY", UtilityParam.X_API_KEY).build()
            }
        }
        val response = chain.proceed(request)
        val body = response.body
        val bodyString = body?.string()
        return response.newBuilder().body(bodyString!!.toResponseBody(body.contentType()))
            .build()
    }
}


