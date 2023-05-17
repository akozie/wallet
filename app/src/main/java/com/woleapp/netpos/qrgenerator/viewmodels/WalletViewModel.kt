package com.woleapp.netpos.qrgenerator.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.ErrorModel
import com.woleapp.netpos.qrgenerator.model.User
import com.woleapp.netpos.qrgenerator.model.wallet.*
import com.woleapp.netpos.qrgenerator.model.wallet.request.SendWithTallyNumberRequest
import com.woleapp.netpos.qrgenerator.network.WalletRepository
import com.woleapp.netpos.qrgenerator.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private var _fetchWalletResponse: MutableLiveData<Resource<WalletModelResponse>> =
        MutableLiveData()
    val fetchWalletResponse: LiveData<Resource<WalletModelResponse>> get() = _fetchWalletResponse

    private var _walletOTPResponse: MutableLiveData<Resource<OTPWalletResponse>> =
        MutableLiveData()
    val walletOTPResponse: LiveData<Resource<OTPWalletResponse>> get() = _walletOTPResponse

    private var _sendWithTallyNumberResponse: MutableLiveData<Resource<SendWithTallyNumberResponse>> =
        MutableLiveData()
    val sendWithTallyNumberResponse: LiveData<Resource<SendWithTallyNumberResponse>> get() = _sendWithTallyNumberResponse

    private var _setPINResponse: MutableLiveData<Resource<GeneralWalletResponse>> =
        MutableLiveData()
    val setPINResponse: LiveData<Resource<GeneralWalletResponse>> get() = _setPINResponse

    private var _getUserTransactionResponse: MutableLiveData<Resource<TallyWalletUserTransactionsResponse>> =
        MutableLiveData()
    val getUserTransactionResponse: LiveData<Resource<TallyWalletUserTransactionsResponse>> get() = _getUserTransactionResponse

    private var _creditWalletResponse: MutableLiveData<Resource<GeneralWalletResponse>> =
        MutableLiveData()
    val creditWalletResponse: LiveData<Resource<GeneralWalletResponse>> get() = _creditWalletResponse

    private val _fetchWalletMessage = MutableLiveData<Event<String>>()
    val fetchWalletMessage: LiveData<Event<String>>
        get() = _fetchWalletMessage


    fun fetchWallet(token : String) {
        _fetchWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchWallet(token)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    Prefs.putString(PREF_TALLY_WALLET, Gson().toJson(it))
                    _fetchWalletResponse.postValue(Resource.success(it))
                   // _fetchWalletMessage.value = Event("User logged in successfully")
                }
                error?.let {
                    _fetchWalletResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                                    ?: "Error"
                            } catch (e: Exception) {
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun verifyWalletOTP(token: String, otp: String) {
        _walletOTPResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.verifyWalletOTP(token, otp)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _walletOTPResponse.postValue(Resource.success(it))
                   // _fetchWalletMessage.value = Event("User logged in successfully")
                }
                error?.let {
                    _walletOTPResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
//                        _fetchWalletMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                    ?: "Error"
//                            } catch (e: Exception) {
//                                "Error"
//                            }
//                        )
                    }
                }
            })
    }

//    fun sendWithTallyNumbe(descAccount: String, transactionAmount: String, transactionPin: String) {
//        _sendWithTallyNumberResponse.postValue(Resource.loading(null))
//        disposable.add(walletRepository.sendWithTallyNumber(descAccount, transactionAmount, transactionPin)
//            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribe { data, error ->
//                data?.let {
//                    _sendWithTallyNumberResponse.postValue(Resource.success(it))
//                   // _fetchWalletMessage.value = Event("User logged in successfully")
//                }
//                error?.let {
//                    _sendWithTallyNumberResponse.postValue(Resource.error(null))
//                    (it as? HttpException).let { httpException ->
//                        val errorMessage = httpException?.response()?.errorBody()?.string()
//                            ?: "{\"message\":\"Unexpected error\"}"
////                        _fetchWalletMessage.value = Event(
////                            try {
////                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
////                                    ?: "Error"
////                            } catch (e: Exception) {
////                                "Error"
////                            }
////                        )
//                    }
//                }
//            })
//    }

    fun sendWithTallyNumber(context:Context, token: String, sendWithTallyNumberRequest: SendWithTallyNumberRequest) =
        walletRepository.sendWithTallyNumber(token, sendWithTallyNumberRequest)
            .flatMap {
                if (it.status == "Success") {
                    Single.just(Resource.success(it))
                } else {
                    Single.just(Resource.error(it))
                }
            }

    fun setTransactionPin(transactionPin: String) {
        _setPINResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.setTransactionPin("Bearer ${Singletons().getTallyUserToken()!!}",transactionPin)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _setPINResponse.postValue(Resource.success(it))
                   // _fetchWalletMessage.value = Event("User logged in successfully")
                }
                error?.let {
                    _setPINResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
//                        _fetchWalletMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                    ?: "Error"
//                            } catch (e: Exception) {
//                                "Error"
//                            }
//                        )
                    }
                }
            })
    }

    fun getUserTransactions(recordsNumber: Int) {
        _getUserTransactionResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getUserTransactions("Bearer ${Singletons().getTallyUserToken()!!}", recordsNumber)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _getUserTransactionResponse.postValue(Resource.success(it))
                   // _fetchWalletMessage.value = Event("User logged in successfully")
                }
                error?.let {
                    _getUserTransactionResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
//                        _fetchWalletMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                    ?: "Error"
//                            } catch (e: Exception) {
//                                "Error"
//                            }
//                        )
                    }
                }
            })
    }

    fun creditWallet(transactionAmount: String, transactionID: String) {
        _creditWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.creditWallet("Bearer ${Singletons().getTallyUserToken()!!}", transactionAmount, transactionID)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _creditWalletResponse.postValue(Resource.success(it))
                }
                error?.let {
                    _creditWalletResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
//                        _fetchWalletMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                    ?: "Error"
//                            } catch (e: Exception) {
//                                "Error"
//                            }
//                        )
                    }
                }
            })
    }


    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}