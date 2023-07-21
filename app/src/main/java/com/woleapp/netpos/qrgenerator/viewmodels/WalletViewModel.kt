package com.woleapp.netpos.qrgenerator.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.ErrorModel
import com.woleapp.netpos.qrgenerator.model.GeneralResponse
import com.woleapp.netpos.qrgenerator.model.pay.PayResponse
import com.woleapp.netpos.qrgenerator.model.wallet.*
import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import com.woleapp.netpos.qrgenerator.network.WalletRepository
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val gson: Gson
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private var _fetchWalletResponse: MutableLiveData<Resource<WalletModelResponse>> =
        MutableLiveData()
    val fetchWalletResponse: LiveData<Resource<WalletModelResponse>> get() = _fetchWalletResponse

    private var _verifyWalletResponse: MutableLiveData<Resource<WalletModelResponse>> =
        MutableLiveData()
    val verifyWalletResponse: LiveData<Resource<WalletModelResponse>> get() = _verifyWalletResponse

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

    private var _creditWalletResponse: MutableLiveData<Resource<CreditWalletCardResponse>> =
        MutableLiveData()
    val creditWalletResponse: LiveData<Resource<CreditWalletCardResponse>> get() = _creditWalletResponse

    private var _getSecurityQuestionsResponse: MutableLiveData<Resource<List<GetSecurityQuestionResponseItem>>> =
        MutableLiveData()
    val getSecurityQuestionsResponse: LiveData<Resource<List<GetSecurityQuestionResponseItem>>> get() = _getSecurityQuestionsResponse

    private var _getOtpToUpdatePinResponse: MutableLiveData<Resource<OtpVerificationToUpdatePinResponse>> =
        MutableLiveData()
    val getOtpToUpdatePinResponse: LiveData<Resource<OtpVerificationToUpdatePinResponse>> get() = _getOtpToUpdatePinResponse

    private var _fetchQrResponse: MutableLiveData<Resource<List<FetchQrTokenResponseItem>>> =
        MutableLiveData()
    val fetchQrResponse: LiveData<Resource<List<FetchQrTokenResponseItem>>> get() = _fetchQrResponse

    private var _storeQrResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()
    val storeQrResponse: LiveData<Resource<GeneralResponse>> get() = _storeQrResponse

    private var _updatePinResponse: MutableLiveData<Resource<GeneralWalletResponse>> =
        MutableLiveData()
    val updatePinResponse: LiveData<Resource<GeneralWalletResponse>> get() = _updatePinResponse

    private var _getTransactionReceiptResponse: MutableLiveData<Resource<TransactionReceiptResponse>> =
        MutableLiveData()
    val getTransactionReceiptResponse: LiveData<Resource<TransactionReceiptResponse>> get() = _getTransactionReceiptResponse

    private var _sendEmailReceiptResponse: MutableLiveData<Resource<TransactionReceiptResponse>> =
        MutableLiveData()
    val sendEmailReceiptResponse: LiveData<Resource<TransactionReceiptResponse>> get() = _sendEmailReceiptResponse

    private var _getSelectedQuestionResponse: MutableLiveData<Resource<GetSelectedQuestionResponse>> =
        MutableLiveData()
    val getSelectedQuestionResponse: LiveData<Resource<GetSelectedQuestionResponse>> get() = _getSelectedQuestionResponse


    private var _fetchOtherAccountResponse: MutableLiveData<Resource<FindAccountNumberResponse>> =
        MutableLiveData()
    val fetchOtherAccountResponse: LiveData<Resource<FindAccountNumberResponse>> get() = _fetchOtherAccountResponse


    private var _fetchProvidusAccountResponse: MutableLiveData<Resource<FindAccountNumberResponse>> =
        MutableLiveData()
    val fetchProvidusAccountResponse: LiveData<Resource<FindAccountNumberResponse>> get() = _fetchProvidusAccountResponse


    private var _providusToProvidusResponse: MutableLiveData<Resource<WithdrawalResponse>> =
        MutableLiveData()
    val providusToProvidusResponse: LiveData<Resource<WithdrawalResponse>> get() = _providusToProvidusResponse


    private var _providusToOtherBanksResponse: MutableLiveData<Resource<WithdrawalResponse>> =
        MutableLiveData()
    val providusToOtherBanksResponse: LiveData<Resource<WithdrawalResponse>> get() = _providusToOtherBanksResponse


    private var _getWalletStatusResponse: MutableLiveData<Resource<WalletStatusResponse>> =
        MutableLiveData()
    val getWalletStatusResponse: LiveData<Resource<WalletStatusResponse>> get() = _getWalletStatusResponse


    private var _getWalletUserResponse: MutableLiveData<Resource<WalletUserResponse>> =
        MutableLiveData()
    val getWalletUserResponse: LiveData<Resource<WalletUserResponse>> get() = _getWalletUserResponse



    private val _fetchWalletMessage = MutableLiveData<Event<String>>()
    val fetchWalletMessage: LiveData<Event<String>>
        get() = _fetchWalletMessage

    private val _fetchWalletP2PMessage = MutableLiveData<String>()
    val fetchWalletP2PMessage: LiveData<String>
        get() = _fetchWalletP2PMessage

    val listOfSecurityQuestions = arrayListOf<GetSecurityQuestionResponseItem>()
    val listOfQrTokens = arrayListOf<FetchQrTokenResponseItem>()
    lateinit var errorMsg: String

    fun fetchWallet(token : String) {
        _fetchWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchWallet(token)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
               //     Prefs.putString(PREF_TALLY_WALLET, Gson().toJson(it))
                    _fetchWalletResponse.postValue(Resource.success(it))
              //      _fetchWalletMessage.value = Event(Resource.success(it).message)
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

    fun verifyWalletNumber(token : String, verifyWallet: Boolean) {
        _verifyWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.verifyWalletNumber(token, verifyWallet)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    Prefs.putString(PREF_TALLY_WALLET, Gson().toJson(it))
                    _verifyWalletResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).data?.message.toString())
                }
                error?.let {
                    _verifyWalletResponse.postValue(Resource.error(null))
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

    fun confirmTransactionPin(token: String, confirmTransactionPin: ConfirmTransactionPin) =
        walletRepository.confirmTransactionPin(token, confirmTransactionPin)
            .flatMap {
                if (it.isSuccessful) {
                    Prefs.putString(CONFIRM_PIN_RESPONSE, it.body()?.pin_correctness.toString())
                    Single.just(Resource.success(it.body()))
                } else {
                    try {
                        val gson = Gson()
                        errorMsg = gson.fromJson(it.errorBody()?.charStream(), ErrorModel::class.java).message
                        Prefs.putString(WALLET_RESPONSE, errorMsg)
                    } catch (e: java.lang.Exception) {
                        //
                    }
                    Single.just(Resource.error(errorMsg))
                }
            }

    fun sendWithTallyNumber(context: Context, token: String, sendWithTallyNumberRequest: SendWithTallyNumberRequest) =
        walletRepository.sendWithTallyNumber(token, sendWithTallyNumberRequest)
            .flatMap {
                if (it.isSuccessful) {
                    Prefs.putString(WALLET_RESPONSE, it.body()?.message)
                    Single.just(Resource.success(it.body()))
                } else {
                    try {
                        val gson = Gson()
                        errorMsg = gson.fromJson(it.errorBody()?.charStream(), ErrorModel::class.java).message
                        Prefs.putString(WALLET_RESPONSE, errorMsg)
                        val anotherTest = Event(errorMsg)
                        val justForTest = Event("YEEEEEEEP")
                      //  Log.d("CHECKRESULT", errorMsg)
                       // Log.d("CHECKRESULT=====>", justForTest.toString())
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    //    _fetchWalletP2PMessage.value = errorMsg
                     //   Log.d("<===CHECKRESULT=====>", _fetchWalletP2PMessage.value.toString())
                    } catch (e: java.lang.Exception) {
                        //
                    }
                    Single.just(Resource.error(errorMsg))
                }
            }


//    fun sendEmailReceipt(token: String, transactionReceiptRequest: TransactionReceiptRequest) =
//        walletRepository.sendEmailReceipt(token, transactionReceiptRequest)
//            .flatMap {
//                if (it.isSuccessful) {
//                    //Prefs.putString(WALLET_RESPONSE, it.body()?.message)
//                    Single.just(Resource.success(it.body()))
//                } else {
//                    try {
//                        val gson = Gson()
//                        errorMsg = gson.fromJson(it.errorBody()?.charStream(), ErrorModel::class.java).message
//                      //  Prefs.putString(WALLET_RESPONSE, errorMsg)
//                    } catch (e: java.lang.Exception) {
//                        //
//                    }
//                    Single.just(Resource.error(errorMsg))
//                }
//            }

    fun setTransactionPin(transactionPin: String, securityQuestionId: String, securityQuestion: String, securityAnswer: String) {
        _setPINResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.setTransactionPin("Bearer ${Singletons().getTallyUserToken()!!}",transactionPin, securityQuestionId, securityQuestion, securityAnswer)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _setPINResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).data!!.message)
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

    fun creditWallet(transactionID: String) {
        _creditWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.creditWallet("Bearer ${Singletons().getTallyUserToken()!!}", transactionID)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _creditWalletResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).data!!.message)
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
               //     _fetchWalletMessage.value = Event(Resource.success(it).data!!.message)
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


//    fun fetchQrToken() {
//        _fetchQrResponse.postValue(Resource.loading(null))
//        disposable.add(walletRepository.fetchQrToken("Bearer ${Singletons().getTallyUserToken()!!}")
//            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//            .subscribe { data, error ->
//                data?.let {
//                    _fetchQrResponse.postValue(Resource.success(it.data))
//                    for (i in 0 until it.data.size) {
//                        listOfQrTokens.add(it.data[i])
//                    }
//                }
//                error?.let {
//                    _fetchQrResponse.postValue(Resource.error(null))
//                    (it as? HttpException).let { httpException ->
//                        val errorMessage = httpException?.response()?.errorBody()?.string()
//                            ?: "{\"message\":\"Unexpected error\"}"
//                        _fetchWalletMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                            } catch (e: Exception) {
//                                "Gateway Time-out"
//                            }
//                        )
//                    }
//                }
//            })
//    }

    fun fetchQrToken(token: String) =
        walletRepository.fetchQrToken(token)
            .flatMap {
                if (it.isSuccessful) {
                //    Prefs.putString(WALLET_RESPONSE, it.body()?.status)
                    it.body()?.data?.let {result ->
                        for (i in result.indices) {
                            listOfQrTokens.add(result[i])
                        }
                    }
                    Single.just(Resource.success(it.body()))
                } else {
                    try {
                        val gson = Gson()
                        errorMsg = gson.fromJson(it.errorBody()?.charStream(), ErrorModel::class.java).message
                        Prefs.putString(WALLET_RESPONSE, errorMsg)
                    } catch (e: java.lang.Exception) {
                        //
                    }
                    Single.just(Resource.error(errorMsg))
                }
            }

    fun storeQrToken(qrTokenRequest: QrTokenRequest) {
        _storeQrResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.storeQrToken("Bearer ${Singletons().getTallyUserToken()!!}", qrTokenRequest)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _storeQrResponse.postValue(Resource.success(it))
                }
                error?.let {
                    _storeQrResponse.postValue(Resource.error(null))
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
                    _fetchWalletMessage.value = Event(Resource.success(it).data!!.message)
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

    fun getTransactionReceipt(transactionReceiptResponse: TransactionReceiptRequest) {
        _getTransactionReceiptResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getTransactionReceipt("Bearer ${Singletons().getTallyUserToken()!!}", transactionReceiptResponse)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _getTransactionReceiptResponse.postValue(Resource.success(it))
                //    _fetchWalletMessage.value = Event(Resource.success(it).data!!.)
                }
                error?.let {
                    _getTransactionReceiptResponse.postValue(Resource.error(null))
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

    fun sendEmailReceipt(transactionReceiptResponse: TransactionReceiptRequest) {
        _sendEmailReceiptResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.sendEmailReceipt("Bearer ${Singletons().getTallyUserToken()!!}", transactionReceiptResponse)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _sendEmailReceiptResponse.postValue(Resource.success(it))
                    _fetchWalletMessage.value = Event(Resource.success(it).data!!.receipt)
                }
                error?.let {
                    _sendEmailReceiptResponse.postValue(Resource.error(null))
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

    fun getSelectedQuestion() {
        _getSelectedQuestionResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getSelectedQuestion("Bearer ${Singletons().getTallyUserToken()!!}")
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _getSelectedQuestionResponse.postValue(Resource.success(it))
                }
                error?.let {
                    _getSelectedQuestionResponse.postValue(Resource.error(null))
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

    fun fetchOtherAccount(accountNumber: FindAccountNumberRequest) {
        _fetchOtherAccountResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchOtherAccount("Bearer ${Singletons().getTallyUserToken()!!}", accountNumber)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _fetchOtherAccountResponse.postValue(Resource.success(it))
                 //  _fetchWalletMessage.value = Event(Resource.success(it).message)
                }
                error?.let {
                    _fetchOtherAccountResponse.postValue(Resource.error(null))
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

    fun fetchProvidusAccount(accountNumber: FindAccountNumberRequest) {
        _fetchProvidusAccountResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchProvidusAccount("Bearer ${Singletons().getTallyUserToken()!!}", accountNumber)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _fetchProvidusAccountResponse.postValue(Resource.success(it))
                  // _fetchWalletMessage.value = Event(Resource.success(it).data.status)
                }
                error?.let {
                    _fetchProvidusAccountResponse.postValue(Resource.error(null))
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

    fun providusToProvidus(providusRequest: ProvidusRequest) {
        _providusToProvidusResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.providusToProvidus("Bearer ${Singletons().getTallyUserToken()!!}", providusRequest)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _providusToProvidusResponse.postValue(Resource.success(it))
                   _fetchWalletMessage.value = Event(Resource.success(it).data!!.Successful)
                }
                error?.let {
                    _providusToProvidusResponse.postValue(Resource.error(null))
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

    fun providusToOtherBanks(otherBanksRequest: OtherBanksRequest) {
        _providusToOtherBanksResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.providusToOtherBanks("Bearer ${Singletons().getTallyUserToken()!!}", otherBanksRequest)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _providusToOtherBanksResponse.postValue(Resource.success(it))
                   _fetchWalletMessage.value = Event(Resource.success(it).data!!.Successful)
                }
                error?.let {
                    _providusToOtherBanksResponse.postValue(Resource.error(null))
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

    fun getWalletStatus() {
        _getWalletStatusResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getWalletStatus("Bearer ${Singletons().getTallyUserToken()!!}")
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    val userExist = JSONObject(it.toString())
                    if (!userExist.getBoolean("exists")) {
                        val noUserExists = gson.fromJson(it, WalletStatusResponse::class.java)
                        _getWalletStatusResponse.postValue(Resource.success(noUserExists))// Do something with the boolean value
                    } else {
                        val noUserExists = gson.fromJson(it, WalletUserResponse::class.java)
                        _getWalletUserResponse.postValue(Resource.success(noUserExists))
                        Prefs.putString(PREF_TALLY_WALLET, Gson().toJson(it))
                    }
                }
                error?.let {
                    _getWalletStatusResponse.postValue(Resource.error(null))
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

    fun clear(){
        _sendEmailReceiptResponse.postValue(Resource.error(null))
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}