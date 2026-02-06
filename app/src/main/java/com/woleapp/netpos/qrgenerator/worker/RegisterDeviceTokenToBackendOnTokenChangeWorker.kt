package com.woleapp.netpos.qrgenerator.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.woleapp.netpos.qrgenerator.model.ErrorModel
import com.woleapp.netpos.qrgenerator.model.updatetoken.StoreDeviceToken
import com.woleapp.netpos.qrgenerator.model.updatetoken.UpdateFirebaseToken
import com.woleapp.netpos.qrgenerator.model.wallet.GeneralWalletResponse
import com.woleapp.netpos.qrgenerator.network.UpdateTokenClient
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.disposeWith
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.getSingleTransformer
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException

class RegisterDeviceTokenToBackendOnTokenChangeWorker(
    val context: Context,
    private val workParameters: WorkerParameters,
) : Worker(context, workParameters) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val updateTokenRepository = UpdateTokenClient.getInstance()

    override fun doWork(): Result {
        val deviceToken = inputData.getString(WORKER_INPUT_FIREBASE_DEVICE_TOKEN_TAG)


        val response = deviceToken?.let { token ->
            var tokenResponse: GeneralWalletResponse? = null
            val oldFirebaseToken = EncryptedPrefsUtils.getString(applicationContext, FIREBASE_TOKEN)
            val req = oldFirebaseToken?.let { UpdateFirebaseToken( old_device_token = it,
                new_device_token = token
            ) }
            if (req != null) {
        //        Log.d("UPDATE_TOKEN", req.toString())
                updateTokenRepository.updateFirebaseToken(
                    "Bearer ${
                        Singletons().getTallyUserToken(
                            context
                        )!!
                    }",
                    UtilityParam.STRING_WALLET_X_API_TOKEN,
                    req
                )
                    .compose(getSingleTransformer(REFRESH_TOKEN_ERROR))
                    .subscribe { value, error ->
                        value?.let {
                            tokenResponse = value
                        }
                        error?.let {
                            (it as? HttpException).let { httpException ->
                                val errorMessage =
                                    httpException?.response()?.errorBody()?.string()
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
                    }.disposeWith(compositeDisposable)
            }
            tokenResponse?.status
        }

        return if (response == "success") {
            Result.success()
        } else {
            Result.retry()
        }

    }
}
