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
import com.woleapp.netpos.qrgenerator.model.pay.PayResponseErrorModel
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

    private var _getSecurityQuestionsResponse: MutableLiveData<Resource<List<GetSecurityQuestionResponseItem>>> =
        MutableLiveData()
    val getSecurityQuestionsResponse: LiveData<Resource<List<GetSecurityQuestionResponseItem>>> get() = _getSecurityQuestionsResponse

    private var _getOtpToUpdatePinResponse: MutableLiveData<Resource<OtpVerificationToUpdatePinResponse>> =
        MutableLiveData()
    val getOtpToUpdatePinResponse: LiveData<Resource<OtpVerificationToUpdatePinResponse>> get() = _getOtpToUpdatePinResponse

    private var _updatePinResponse: MutableLiveData<Resource<GeneralWalletResponse>> =
        MutableLiveData()
    val updatePinResponse: LiveData<Resource<GeneralWalletResponse>> get() = _updatePinResponse



    private val _fetchWalletMessage = MutableLiveData<Event<String>>()
    val fetchWalletMessage: LiveData<Event<String>>
        get() = _fetchWalletMessage

    val listOfSecurityQuestions = arrayListOf<GetSecurityQuestionResponseItem>()


    fun fetchWallet(token : String) {
        _fetchWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchWallet(token)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    Prefs.putString(PREF_TALLY_WALLET, Gson().toJson(it))
                    _fetchWalletResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
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
                    _fetchWalletMessage.value = Event(Resource.success(it).data!!.message)
                }
                error?.let {
                    _walletOTPResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }

    fun sendWithTallyNumber(token: String, sendWithTallyNumberRequest: SendWithTallyNumberRequest) =
        walletRepository.sendWithTallyNumber(token, sendWithTallyNumberRequest)
            .flatMap {
                if (it.status == "Success") {
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
                    Single.just(Resource.success(it))
                } else {
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
                    Single.just(Resource.error(it))
                }
            }

    fun setTransactionPin(transactionPin: String,securityQuestion: String, securityAnswer: String) {
        _setPINResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.setTransactionPin("Bearer ${Singletons().getTallyUserToken()!!}",transactionPin, securityQuestion, securityAnswer)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _setPINResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
                }
                error?.let {
                    _setPINResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
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
                }
                error?.let {
                    _getUserTransactionResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
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
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
                }
                error?.let {
                    _creditWalletResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }

    fun getOtpVerificationToUpdatePin() {
        _getOtpToUpdatePinResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getOtpVerificationToUpdatePin("Bearer ${Singletons().getTallyUserToken()!!}")
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _getOtpToUpdatePinResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
                }
                error?.let {
                    _getOtpToUpdatePinResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }


    fun getSecurityQuestions() {
        _getSecurityQuestionsResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getSecurityQuestions("Bearer ${Singletons().getTallyUserToken()!!}")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    Log.d("SECURITYQUESTIONSRESULT", it.toString())
                    _getSecurityQuestionsResponse.postValue(Resource.success(it))
                    for (i in 0 until it.size) {
                        listOfSecurityQuestions.add(it[i])
                    }
                }
                error?.let {
                    _getSecurityQuestionsResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }

    fun updateTransactionPin(newPin: String, otp: String, securityAnswer: String, securityQuestion: String) {
        _updatePinResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.updateTransactionPin("Bearer ${Singletons().getTallyUserToken()!!}", newPin, otp, securityAnswer, securityQuestion)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _updatePinResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).message)
                }
                error?.let {
                    _updatePinResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}