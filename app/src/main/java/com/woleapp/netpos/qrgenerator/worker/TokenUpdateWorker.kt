package com.woleapp.netpos.qrgenerator.worker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.ErrorModel
import com.woleapp.netpos.qrgenerator.model.updatetoken.NewTokenRequest
import com.woleapp.netpos.qrgenerator.model.updatetoken.NewTokenResponse
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.disposeWith
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.getSingleTransformer
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException

class TokenUpdateWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val qrRepository = QRClient.getInstance()

    override fun doWork(): Result {
        val email = Singletons().getCurrentlyLoggedInUser(applicationContext)?.email
        val refreshToken = Singletons().getRefreshToken(applicationContext)!!

        val handler = Handler(Looper.getMainLooper())

//        handler.post {
//            Toast.makeText(applicationContext, "000", Toast.LENGTH_SHORT).show()
//        }
        val response = refreshToken.let { _ ->
            var tokenResponse: NewTokenResponse? = null
            email?.let { email ->
                val req = NewTokenRequest(email, refreshToken)
                qrRepository.getNewToken(req)
                    .compose(getSingleTransformer(REFRESH_TOKEN_ERROR))
                    .subscribe { value, error ->
                        value?.let {
                            tokenResponse = value
                            EncryptedPrefsUtils.putString(applicationContext, USER_TOKEN, value.token)
                        }
                        error?.let {
                            (it as? HttpException).let { httpException ->
                                val errorMessage = httpException?.response()?.errorBody()?.string()
                                    ?: "{\"message\":\"Unexpected error\"}"
                                val registerMessage = Event(
                                    try {
                                        Gson().fromJson(
                                            errorMessage,
                                            ErrorModel::class.java
                                        ).message
                                            ?: "Error"
                                    } catch (e: Exception) {
                                        "Error"
                                    }
                                )
                            }
                        }
                    }.disposeWith(disposable)
            }
            tokenResponse?.success
        } ?: false

        return if (response) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
