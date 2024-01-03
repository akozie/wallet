package com.woleapp.netpos.qrgenerator.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.model.*
import com.woleapp.netpos.qrgenerator.model.referrals.ConfirmReferralModel
import com.woleapp.netpos.qrgenerator.model.referrals.InviteToTallyFailureResponse
import com.woleapp.netpos.qrgenerator.model.referrals.InviteToTallyModel
import com.woleapp.netpos.qrgenerator.model.wallet.*
import com.woleapp.netpos.qrgenerator.model.wallet.request.*
import com.woleapp.netpos.qrgenerator.network.WalletRepository
import com.woleapp.netpos.qrgenerator.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

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

    private var _inViteToTallyResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()
    val inViteToTallyResponse: LiveData<Resource<GeneralResponse>> get() = _inViteToTallyResponse

    private var _confirmReferralResponse: MutableLiveData<Resource<ConfirmReferralResponse>> =
        MutableLiveData()
    val confirmReferralResponse: LiveData<Resource<ConfirmReferralResponse>> get() = _confirmReferralResponse

    private var _walletLoginResponse: MutableLiveData<Resource<WalletLoginResponse>> =
        MutableLiveData()
    val walletLoginResponse: LiveData<Resource<WalletLoginResponse>> get() = _walletLoginResponse


    private var _getWalletUserResponse: MutableLiveData<Resource<WalletUserResponse>> =
        MutableLiveData()
    val getWalletUserResponse: LiveData<Resource<WalletUserResponse>> get() = _getWalletUserResponse


    private val _fetchWalletMessage = MutableLiveData<Event<String>>()
    val fetchWalletMessage: LiveData<Event<String>>
        get() = _fetchWalletMessage

    private val _inviteToTallyMessage = MutableLiveData<Event<String>>()
    val inviteToTallyMessage: LiveData<Event<String>>
        get() = _inviteToTallyMessage

    private val _confirmReferralMessage = MutableLiveData<Event<String>>()
    val confirmReferralMessage: LiveData<Event<String>>
        get() = _confirmReferralMessage

    private val _fetchWalletP2PMessage = MutableLiveData<String>()
    val fetchWalletP2PMessage: LiveData<String>
        get() = _fetchWalletP2PMessage

    val listOfSecurityQuestions = arrayListOf<GetSecurityQuestionResponseItem>()
    val listOfQrTokens = arrayListOf<FetchQrTokenResponseItem>()
    lateinit var errorMsg: String

    private lateinit var formattedDate: String

    fun fetchWallet(token: String) {
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

    fun verifyWalletNumber(context: Context, token: String, verifyWallet: Boolean) {
        _verifyWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.verifyWalletNumber(token, verifyWallet)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    EncryptedPrefsUtils.putString(context, PREF_TALLY_WALLET, Gson().toJson(it))
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
                                "Error"
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
                        errorMsg = gson.fromJson(
                            it.errorBody()?.charStream(),
                            ErrorModel::class.java
                        ).message
                        Prefs.putString(WALLET_RESPONSE, errorMsg)
                    } catch (e: java.lang.Exception) {
                        //
                    }
                    Single.just(Resource.error(errorMsg))
                }
            }

    fun sendWithTallyNumber(
        context: Context,
        token: String,
        sendWithTallyNumberRequest: SendWithTallyNumberRequest
    ) =
        walletRepository.sendWithTallyNumber(token, sendWithTallyNumberRequest)
            .flatMap {
                if (it.isSuccessful) {
                    EncryptedPrefsUtils.putString(
                        context,
                        WALLET_RESPONSE,
                        it.body()?.message.toString()
                    )
                    Single.just(Resource.success(it.body()))
                } else {
                    try {
                        val gson = Gson()
                        errorMsg = gson.fromJson(
                            it.errorBody()?.charStream(),
                            ErrorModel::class.java
                        ).message
                        EncryptedPrefsUtils.putString(context, WALLET_RESPONSE, errorMsg)
                        //   Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    } catch (e: java.lang.Exception) {
                        //
                    }
                    Single.just(Resource.error(errorMsg))
                }
            }

    fun setTransactionPin(
        context: Context,
        transactionPin: String,
        securityQuestionId: String,
        securityQuestion: String,
        securityAnswer: String
    ) {
        _setPINResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.setTransactionPin(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            transactionPin,
            securityQuestionId,
            securityQuestion,
            securityAnswer
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun getUserTransactions(context: Context, recordsNumber: Int) {
        _getUserTransactionResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getUserTransactions(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            recordsNumber
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun creditWallet(context: Context, transactionID: String) {
        _creditWalletResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.creditWallet(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            transactionID
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun getOtpVerificationToUpdatePin(context: Context) {
        _getOtpToUpdatePinResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getOtpVerificationToUpdatePin(
            "Bearer ${
                Singletons().getTallyUserToken(
                    context
                )!!
            }"
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }


    fun storeQrToken(context: Context, qrTokenRequest: QrTokenRequest) {
        _storeQrResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.storeQrToken(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            qrTokenRequest
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }


//    fun getSecurityQuestions() {
//        _getSecurityQuestionsResponse.postValue(Resource.loading(null))
//        disposable.add(walletRepository.getSecurityQuestions("Bearer ${Singletons().getTallyUserToken()!!}")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
//                data?.let {
//                    Log.d("SECURITYQUESTIONSRESULT", it.toString())
//                    _getSecurityQuestionsResponse.postValue(Resource.success(it))
//                    for (i in 0 until it.size) {
//                        listOfSecurityQuestions.add(it[i])
//                    }
//                }
//                error?.let {
//                    _getSecurityQuestionsResponse.postValue(Resource.error(null))
//                    (it as? HttpException).let { httpException ->
//                        val errorMessage = httpException?.response()?.errorBody()?.string()
//                            ?: "{\"message\":\"Unexpected error\"}"
//                        _fetchWalletMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                            } catch (e: Exception) {
//                                "Error"
//                            }
//                        )
//                    }
//                }
//            })
//    }

    fun updateTransactionPin(
        context: Context,
        oldPin: String,
        newPin: String,
        otp: String,
        securityAnswer: String,
        securityQuestion: String
    ) {
        _updatePinResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.updateTransactionPin(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            oldPin,
            newPin,
            otp,
            securityAnswer,
            securityQuestion,
            Singletons().getAdminAccessToken(context)!!,
            Singletons().getWalletUserTokenId(context)!!,
            Singletons().getAccountId(context)!!
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun sendEmailReceipt(context: Context, transactionReceiptResponse: TransactionReceiptRequest) {
        _sendEmailReceiptResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.sendEmailReceipt(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            transactionReceiptResponse
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun getSelectedQuestion(context: Context) {
        _getSelectedQuestionResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getSelectedQuestion(
            "Bearer ${
                Singletons().getTallyUserToken(
                    context
                )!!
            }"
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun fetchOtherAccount(context: Context, accountNumber: FindAccountNumberRequest) {
        _fetchOtherAccountResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchOtherAccount(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            accountNumber
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun fetchProvidusAccount(context: Context, accountNumber: FindAccountNumberRequest) {
        _fetchProvidusAccountResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.fetchProvidusAccount(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            accountNumber
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun providusToProvidus(context: Context, providusRequest: ProvidusRequest) {
        _providusToProvidusResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.providusToProvidus(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            providusRequest
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun providusToOtherBanks(context: Context, otherBanksRequest: OtherBanksRequest) {
        _providusToOtherBanksResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.providusToOtherBanks(
            "Bearer ${Singletons().getTallyUserToken(context)!!}",
            otherBanksRequest
        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun confirmRef(context: Context, confirmReferralModel: ConfirmReferralModel) {
        _confirmReferralResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.confirmReferral(
            confirmReferralModel
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _confirmReferralResponse.postValue(Resource.success(it))
                    _confirmReferralMessage.value = Event(Resource.success(it).data!!.message)
                    Log.d("CONFIRMED", Event(Resource.success(it).data!!.message).toString())
                }
                error?.let {
                    _confirmReferralResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _confirmReferralMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun walletLogin(context: Context, loginRequest: WalletLoginRequest) {
        _walletLoginResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.walletLogin(
            loginRequest
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _walletLoginResponse.postValue(Resource.success(it))
                    EncryptedPrefsUtils.putString(context, USER_TOKEN_ID, it.data.userTokenId)
                    EncryptedPrefsUtils.putString(context, ACCOUNT_ID, it.data.accountId)
                    EncryptedPrefsUtils.putString(context, ADMIN_ACCESS_TOKEN, it.data.adminAccessToken)
                }
                error?.let {
                    _walletLoginResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _confirmReferralMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun getWalletStatus(context: Context) {
        _getWalletStatusResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.getWalletStatus(
            "Bearer ${
                Singletons().getTallyUserToken(
                    context
                )!!
            }"
        )
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
                        EncryptedPrefsUtils.putString(
                            context,
                            PREF_TALLY_WALLET,
                            Gson().toJson(it)
                        )
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
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun inviteToTallyInFragment(context: Context, inviteToTallyModel: InviteToTallyModel) {
        _inViteToTallyResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.inviteToTally(
            "Bearer ${
                Singletons().getTallyUserToken(
                    context
                )!!
            }", inviteToTallyModel
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    // val userExist = JSONObject(it.toString())
                    if (it.code() != 200) {
                        _inviteToTallyMessage.value = Event("Failed to send invite")
                        _fetchWalletMessage.value = Event("Failed to send invite")
                    } else {
                        val resp = JSONObject(it.body().toString())
                        val status = resp.getString("status")
                        if (status.toString() != "Partial Success") {
                            val successResponse =
                                gson.fromJson(it.body().toString(), GeneralResponse::class.java)
                            val errorResponse = gson.fromJson(
                                resp.toString(),
                                InviteToTallyFailureResponse::class.java
                            )
                            _inViteToTallyResponse.postValue(Resource.success(successResponse))
                            _inviteToTallyMessage.value = Event(successResponse.message)
                            _inviteToTallyMessage.value = Event("Invite sent successfully!")
                        } else {
                            val errorResponse = gson.fromJson(
                                resp.toString(),
                                InviteToTallyFailureResponse::class.java
                            )
                            _inViteToTallyResponse.postValue(
                                Resource.success(
                                    GeneralResponse(
                                        true,
                                        errorResponse.failures.toString()
                                    )
                                )
                            )
                            val failureResponse = errorResponse.failures.toString()
                            val failedInvites = errorResponse.failures?.size!!
                            val successfulInvites = errorResponse.successful_invites?.size!!
                            if (successfulInvites == 1 && failedInvites == 1) {
                                _inviteToTallyMessage.value =
                                    Event("$successfulInvites invite sent successfully \n $failedInvites invite could not be sent")
                            } else if (successfulInvites > 1 && failedInvites > 1) {
                                _inviteToTallyMessage.value =
                                    Event("$successfulInvites invites sent successfully \n $failedInvites invites could not be sent")
                            } else if (failedInvites == 1) {
                                _inviteToTallyMessage.value =
                                    Event("$failedInvites invite could not be sent")
                            } else if (successfulInvites == 1) {
                                _inviteToTallyMessage.value =
                                    Event("$successfulInvites invite sent successfully")
                            } else if (failedInvites > 1) {
                                _inviteToTallyMessage.value =
                                    Event("$failedInvites invites could not be sent")
                            } else if (successfulInvites > 1) {
                                _inviteToTallyMessage.value =
                                    Event("$successfulInvites invites sent successfully")
                            }
                        }
                    }
                }
                error?.let {
                    _inViteToTallyResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Error"
                            }
                        )
                    }
                }
            })
    }

    fun inviteToTally(context: Context, inviteToTallyModel: InviteToTallyModel) {
        _inViteToTallyResponse.postValue(Resource.loading(null))
        disposable.add(walletRepository.inviteToTally(
            "Bearer ${
                Singletons().getTallyUserToken(
                    context
                )!!
            }", inviteToTallyModel
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    // val userExist = JSONObject(it.toString())
                    if (it.code() != 200) {
                        _inviteToTallyMessage.value = Event("Failed to send invite")
                        _fetchWalletMessage.value = Event("Failed to send invite")
                    } else {
                        val resp = JSONObject(it.body().toString())
                        val successfulInvites = resp.getString("status")
                        if (successfulInvites.toString() != "Partial Success") {
                            val successResponse =
                                gson.fromJson(it.body().toString(), GeneralResponse::class.java)
                            _inViteToTallyResponse.postValue(Resource.success(successResponse))
                            _fetchWalletMessage.value = Event(successResponse.message)

                        } else {
                            val errorResponse = gson.fromJson(
                                resp.toString(),
                                InviteToTallyFailureResponse::class.java
                            )
                            _inViteToTallyResponse.postValue(
                                Resource.success(
                                    GeneralResponse(
                                        true,
                                        errorResponse.failures.toString()
                                    )
                                )
                            )// Do something with the boolean value
                            _fetchWalletMessage.value = Event(errorResponse.failures.toString())

                        }
                    }
                }
                error?.let {
                    _inViteToTallyResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _fetchWalletMessage.value = Event(
                            try {
                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
                            } catch (e: Exception) {
                                "Error"
                            }
                        )
                    }
                }
            })
    }

//    fun confirmReferral(context: Context, confirmReferralModel: ConfirmReferralModel) =
//        walletRepository.confirmReferral(
//            "Bearer ${Singletons().getTallyUserToken(context)!!}",
//            confirmReferralModel
//        ).flatMap {
//            if (it.code() != 200) {
//              //  try {
//                    val gson = Gson()
//
//                    val errorMsg = gson.fromJson(
//                        it.errorBody()?.charStream(),
//                        ConfirmReferralResponse::class.java
//                    ).message
//                    Log.d("SUPPOSED", "SUPPOSED")
//                    Log.d("ERRSUPPOSED", errorMsg)
//                    _confirmReferralMessage.value = Event("You previously confirmed 0852444122633 as your referral")
//                   // EncryptedPrefsUtils.putString(context, WALLET_RESPONSE, errorMsg)
//                Single.just(Resource.error(errorMsg))
//            } else {
//                if (it.body()?.has("failures") == true) {
//                    val errorResponse =
//                        gson.fromJson(
//                            it.body().toString(),
//                            InviteToTallyFailureResponse::class.java
//                        )
//                    EncryptedPrefsUtils.putString(
//                        context,
//                        WALLET_RESPONSE,
//                        errorResponse.message
//                    )
//                    Single.just(Resource.success(errorResponse))
//                } else {
//                    val successResponse =
//                        gson.fromJson(it.body().toString(), GeneralResponse::class.java)
//                    EncryptedPrefsUtils.putString(
//                        context,
//                        WALLET_RESPONSE,
//                        successResponse.message
//                    )
//                    Single.just(Resource.success(successResponse))
//                }
//            }
//        }


    fun clear() {
        _sendEmailReceiptResponse.postValue(Resource.error(null))
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}