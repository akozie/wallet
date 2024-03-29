package com.woleapp.netpos.qrgenerator.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.model.*
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutModel
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutResponse
import com.woleapp.netpos.qrgenerator.model.pay.PayResponse
import com.woleapp.netpos.qrgenerator.model.pay.PayResponseErrorModel
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.model.updatetoken.NewTokenResponse
import com.woleapp.netpos.qrgenerator.model.verve.PostQrToServerVerveResponseModel
import com.woleapp.netpos.qrgenerator.model.verve.SendOtpForVerveCardModel
import com.woleapp.netpos.qrgenerator.model.verve.VerveOTPResponse
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponseItem
import com.woleapp.netpos.qrgenerator.network.QRRepository
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.createClientDataForNonVerveCard
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.createClientDataForVerveCard
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.stringToBase64
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.net.SocketException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val qrRepository: QRRepository,
    private val disposable: CompositeDisposable,
    private val gson: Gson
) : ViewModel() {

    private var _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

    private var _logoutResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val logoutResponse: LiveData<Resource<GeneralResponse>> get() = _logoutResponse

    private var _registerResponse: MutableLiveData<Resource<GeneralResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<GeneralResponse>> get() = _registerResponse

    private var _getNewTokenResponse: MutableLiveData<Resource<NewTokenResponse>> =
        MutableLiveData()
    val getNewTokenResponse: LiveData<Resource<NewTokenResponse>> get() = _getNewTokenResponse

    private var _generateQrResponse: MutableLiveData<Resource<GenerateQRResponse>> =
        MutableLiveData()
    val generateQrResponse: LiveData<Resource<GenerateQRResponse>> get() = _generateQrResponse

    private var _cardSchemeResponse: MutableLiveData<Resource<CardSchemeResponse>> =
        MutableLiveData()
    val cardSchemeResponse: LiveData<Resource<CardSchemeResponse>> get() = _cardSchemeResponse

    private var _issuingBankResponse: MutableLiveData<Resource<BankCardResponse>> =
        MutableLiveData()
    val issuingBankResponse: LiveData<Resource<BankCardResponse>> get() = _issuingBankResponse


    private val _checkOutRResponse: MutableLiveData<Resource<CheckOutResponse>> = MutableLiveData()
    val checkOutRResponse: LiveData<Resource<CheckOutResponse>> get() = _checkOutRResponse

    private val _payResponse: MutableLiveData<Resource<PayResponse>> = MutableLiveData()
    val payResponse: LiveData<Resource<PayResponse>> get() = _payResponse

    private val _payVerveResponse: MutableLiveData<Resource<PostQrToServerVerveResponseModel>> =
        MutableLiveData()
    val payVerveResponse: LiveData<Resource<PostQrToServerVerveResponseModel>> get() = _payVerveResponse

    private var _transactionResponse: MutableLiveData<Resource<TransactionResponse>> =
        MutableLiveData()
    val transactionResponse: LiveData<Resource<TransactionResponse>> get() = _transactionResponse

    private val _transactionResponseFromVerve: MutableLiveData<Resource<Any?>> =
        MutableLiveData()
    val transactionResponseFromVerve: LiveData<Resource<Any?>> get() = _transactionResponseFromVerve

    private var _fetchQrResponse: MutableLiveData<Resource<List<FetchQrTokenResponseItem>>> =
        MutableLiveData()
    val fetchQrResponse: LiveData<Resource<List<FetchQrTokenResponseItem>>> get() = _fetchQrResponse


    private val _isVerveCard: MutableLiveData<Boolean> = MutableLiveData(false)


    private val _generateQrMessage = MutableLiveData<Event<String>>()
    val generateQrMessage: LiveData<Event<String>>
        get() = _generateQrMessage

    private val _registerMessage = MutableLiveData<Event<String>>()
    val registerMessage: LiveData<Event<String>>
        get() = _registerMessage


    private val _loginMessage = MutableLiveData<Event<String>>()
    val loginMessage: LiveData<Event<String>>
        get() = _loginMessage


    // FOR QRt
    private val _qrTransactionResponseFromWebView: MutableLiveData<QrTransactionResponseModel> =
        MutableLiveData()
    val qrTransactionResponseFromWebView: LiveData<QrTransactionResponseModel> get() = _qrTransactionResponseFromWebView

    private val _showQrPrintDialog = MutableLiveData<Event<String>>()
    val showQrPrintDialog: LiveData<Event<String>>
        get() = _showQrPrintDialog


    var displayQrStatus = 0

    private val _transactionMessage = MutableLiveData<Event<String>>()
    val transactionMessage: LiveData<Event<String>>
        get() = _transactionMessage

    private val _merchantMessage = MutableLiveData<Event<String>>()
    val merchantMessage: LiveData<Event<String>>
        get() = _merchantMessage

    private val _payMessage = MutableLiveData<Event<String>>()
    val payMessage: LiveData<Event<String>>
        get() = _payMessage

    private val _logoutMessage = MutableLiveData<Event<String>>()
    val logoutMessage: LiveData<Event<String>>
        get() = _logoutMessage

    val cardSchemes = arrayListOf<Row>()

    val issuingBank = arrayListOf<RowX>()


    private lateinit var formattedDate: String


    fun login(context: Context, loginRequest: LoginRequest) {
        _loginResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.login(loginRequest).flatMap {
            Timber.e(it.toString())
            if (!it.success) {
                throw Exception("Login Failed, Check Credentials")
            }
            EncryptedPrefsUtils.putString(context, REFRESH_TOKEN, it.refreshToken)
            val userToken = it.token
            EncryptedPrefsUtils.putString(context, USER_TOKEN, userToken)
            val userTokenDecoded = JWT(userToken)
            val user = User().apply {
                this.fullname =
                    if (userTokenDecoded.claims.containsKey("fullname")) userTokenDecoded.getClaim(
                        "fullname"
                    ).asString().toString() else " "
                this.mobile_phone =
                    if (userTokenDecoded.claims.containsKey("mobile_phone")) userTokenDecoded.getClaim(
                        "mobile_phone"
                    ).asString().toString() else " "
                this.email =
                    if (userTokenDecoded.claims.containsKey("email")) userTokenDecoded.getClaim(
                        "email"
                    ).asString().toString() else " "
                this.id =
                    if (userTokenDecoded.claims.containsKey("id")) userTokenDecoded.getClaim(
                        "id"
                    ).asInt()!! else 1
            }
            Single.just(user)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    EncryptedPrefsUtils.putString(context, PREF_USER, Gson().toJson(it))
                    _loginResponse.postValue(Resource.success(it) as Resource<LoginResponse>)
                    _loginMessage.value = Event("User logged in successfully")
                }
                error?.let {
                    if (it is SocketException && it.message == "Socket closed") {
                        Log.d("SOCK", it.localizedMessage)
                    }
                    _loginResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _loginMessage.value = Event(
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

    fun logout(logoutRequest: JsonObject?) {
        _logoutResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.logout(logoutRequest).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    _logoutResponse.postValue(Resource.success(it))
                    _logoutMessage.value = Event("Logout Successful")
                }
                error?.let {
                    _logoutResponse.postValue(Resource.error(null))
                    // Timber.d("ERROR==${it.localizedMessage}")
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _logoutMessage.value = Event(
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

    fun register(registerRequest: RegisterRequest) {
        _registerResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.register(registerRequest).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    _registerResponse.postValue(Resource.success(it))
                    _registerMessage.value = Event("Registration Successful")
                }
                error?.let {
                    _registerResponse.postValue(Resource.error(null))
                    // Timber.d("ERROR==${it.localizedMessage}")
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _registerMessage.value = Event(
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

//    fun getNewToken(newTokenRequest: NewTokenRequest) {
//        _getNewTokenResponse.postValue(Resource.loading(null))
//        disposable.add(qrRepository.getNewToken(newTokenRequest).subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
//                data?.let {
//                    _getNewTokenResponse.postValue(Resource.success(it))
//                    Prefs.putString(USER_TOKEN, it.token)
//                    Log.d("NEWT", it.token)
//                    //  _registerMessage.value = Event("Registration Successful")
//                }
//                error?.let {
//                    _getNewTokenResponse.postValue(Resource.error(null))
//                    // Timber.d("ERROR==${it.localizedMessage}")
//                    (it as? HttpException).let { httpException ->
//                        val errorMessage = httpException?.response()?.errorBody()?.string()
//                            ?: "{\"message\":\"Unexpected error\"}"
//                        _registerMessage.value = Event(
//                            try {
//                                Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                    ?: "Error"
//                            } catch (e: Exception) {
//                                "Error"
//                            }
//                        )
//                    }
//                }
//            })
//    }

    fun generateQR(qrModelRequest: QrModelRequest, context: Context) {
        _generateQrResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.generateQR(qrModelRequest).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    _generateQrResponse.postValue(Resource.success(it))
                    _generateQrMessage.value = Event("Success")
                    val c = Calendar.getInstance().time
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                    formattedDate = formatter.format(c)
                    val userId = Singletons().getCurrentlyLoggedInUser(context)?.id.toString()
                    val generalResponse = GenerateQRResponse(
                        qr_code_id = it.qr_code_id,
                        data = it.data,
                        success = true,
                        card_scheme = it.card_scheme,
                        issuing_bank = it.issuing_bank,
                        date = formattedDate
                    )
                    if (userId.isNotEmpty()) {
                        val dataQREntity = DomainQREntity(it.qr_code_id!!, userId, generalResponse)
                        AppDatabase.getDatabaseInstance(context).getQrDao()
                            .insertQrCode(dataQREntity)
                    }
                }
                error?.let {
                    _generateQrResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _generateQrMessage.value = Event(
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

    fun getCardSchemes() {
        _cardSchemeResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(qrRepository.getCardSchemes().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    _cardSchemeResponse.postValue(Resource.success(it))
                    //  _message.value = Event("Success")
                    for (i in 0 until it.rows.size) {
                        it.rows[i].let { it1 -> cardSchemes.add(it1) }

                    }
                }
                error?.let {
                    _cardSchemeResponse.postValue(Resource.error(null))
                }
            })
    }

    fun fetchQrToken(context: Context) {
        _fetchQrResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.fetchQrToken()
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    _fetchQrResponse.postValue(Resource.success(it.data))
                    val listOfQrTokens = it.data
                    for (i in listOfQrTokens.indices) {
                        val c = Calendar.getInstance().time
                        val formatter =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                        formattedDate = formatter.format(c)
                        val userId = Singletons().getCurrentlyLoggedInUser(context)?.id.toString()
                        val generalResponse = GenerateQRResponse(
                            qr_code_id = listOfQrTokens[i].qr_code_id,
                            data = listOfQrTokens[i].qr_token,
                            success = true,
                            card_scheme = listOfQrTokens[i].card_scheme,
                            issuing_bank = listOfQrTokens[i].issuing_bank,
                            date = formattedDate
                        )
                        if (userId.isNotEmpty()) {
                            val dataQREntity = DomainQREntity(
                                listOfQrTokens[i].qr_code_id!!,
                                userId,
                                generalResponse
                            )
                            AppDatabase.getDatabaseInstance(context).getQrDao()
                                .insertQrCode(dataQREntity)
                        }
                    }
                }
                error?.let {
                    _fetchQrResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _generateQrMessage.value = Event(
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

    fun getCardBanks() {
        _issuingBankResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.getCardBanks().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    _issuingBankResponse.postValue(Resource.success(it))
                    //     _message.value = Event("Success")
                    for (i in 0 until it.rows.size) {
                        it.rows[i].let { it1 -> issuingBank.add(it1) }
                    }
                }
                error?.let {
                    _issuingBankResponse.postValue(Resource.error(null))
                }
            })
    }

    fun getEachTransaction(qrCodeID: String) {
        _transactionResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.getAllTransaction(qrCodeID).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { data, error ->
                data?.let {
                    _transactionResponse.postValue(Resource.success(it))
                }
                error?.let {
                    _transactionResponse.postValue(Resource.error(null))
                    // Timber.d("ERROR==${it.localizedMessage}")
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _transactionMessage.value = Event(
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

    fun payQrCharges(
        context: Context,
        checkOutModel: CheckOutModel, qrModelRequest: QrModelRequest, pin: String = ""
    ) {

        _payResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.checkOut(checkOutModel).flatMap {
            saveTransIDAndAmountResponse(
                context,
                CheckOutResponse(
                    it.amount,
                    it.customerId,
                    it.domain,
                    it.merchantId,
                    it.status,
                    it.transId
                )
            )
            val clientDataString =
                if (qrModelRequest.card_scheme.contains(
                        "verve",
                        true
                    )
                ) createClientDataForVerveCard(
                    qrModelRequest,
                    pin,
                    it.transId
                )
                else createClientDataForNonVerveCard(
                    it.transId,
                    qrModelRequest.card_number,
                    qrModelRequest.card_expiry,
                    qrModelRequest.card_cvv
                )
            val clientData = stringToBase64(clientDataString)
            val newClientData = clientData.replace("\n", "")
            qrRepository.pay(newClientData)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    val masterVisaResponse = gson.fromJson(it, PayResponse::class.java)
                    if (it.has("TermUrl")) {
                        _payResponse.postValue(Resource.success(masterVisaResponse))
                    }
                    val failedResponse = gson.fromJson(it, PayResponseErrorModel::class.java)
                    if (failedResponse.code == "90") {
                        _payMessage.value = Event(
                            Resource.success(failedResponse).data!!.result
                        )
                        _payResponse.postValue(Resource.success(null))
                    }
                }
                error?.let {
                    _payResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _payMessage.value = Event(
                            try {
                                Gson().fromJson(
                                    errorMessage,
                                    PayResponseErrorModel::class.java
                                ).result
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }

    fun payQrChargesForVerve(
        context: Context,
        checkOutModel: CheckOutModel, qrModelRequest: QrModelRequest, pin: String = ""
    ) {
        _payVerveResponse.postValue(Resource.loading(null))
        disposable.add(qrRepository.checkOut(checkOutModel).flatMap {
            saveTransIDAndAmountResponse(
                context,
                CheckOutResponse(
                    it.amount,
                    it.customerId,
                    it.domain,
                    it.merchantId,
                    it.status,
                    it.transId
                )
            )
            val clientDataString =
                if (qrModelRequest.card_scheme.contains(
                        "verve",
                        true
                    )
                ) createClientDataForVerveCard(
                    qrModelRequest,
                    pin,
                    it.transId
                )
                else createClientDataForNonVerveCard(
                    it.transId,
                    qrModelRequest.card_number,
                    qrModelRequest.card_expiry,
                    qrModelRequest.card_cvv
                )
            val clientData = stringToBase64(clientDataString)
            val newClientData = clientData.replace("\n", "")
            qrRepository.pay(newClientData)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { data, error ->
                data?.let {
                    val verveResponse =
                        gson.fromJson(it, PostQrToServerVerveResponseModel::class.java)
                    _payVerveResponse.postValue(Resource.success(verveResponse))
                    val failedResponse = gson.fromJson(it, PayResponseErrorModel::class.java)
                    if (failedResponse.code == "90") {
                        _payMessage.value = Event(
                            Resource.success(failedResponse).data!!.result
                        )
                    }
                }
                error?.let {
                    _payVerveResponse.postValue(Resource.error(null))
                    (it as? HttpException).let { httpException ->
                        val errorMessage = httpException?.response()?.errorBody()?.string()
                            ?: "{\"message\":\"Unexpected error\"}"
                        _payMessage.value = Event(
                            try {
                                Gson().fromJson(
                                    errorMessage,
                                    PayResponseErrorModel::class.java
                                ).result
                            } catch (e: Exception) {
                                "Gateway Time-out"
                            }
                        )
                    }
                }
            })
    }

    fun sendOtpForVerveCard(otp: String) {
        _transactionResponseFromVerve.value = Resource.loading(null)
        _payVerveResponse.value?.data?.let {
            val otpDetails =
                stringToBase64(it.transId + ":" + it.result + ":" + otp.trim() + ":" + it.provider)
            var result = it.transId + ":" + it.result + ":" + otp.trim() + ":" + it.provider
            val newClientDataForVerve = otpDetails.replace("\n", "")
            val otpPayLoad = SendOtpForVerveCardModel(newClientDataForVerve)
            disposable.add(
                qrRepository.consummateTransactionBySendingOtp(otpPayLoad)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { response ->
                        Single.just(response.body())
                    }
                    .subscribe { data, error ->
                        data?.let { transResp ->
                            val transResponse =
                                gson.fromJson(transResp, VerveOTPResponse::class.java)
//                            } else {
//                                gson.fromJson(transResp, PayResponse::class.java)
//                            }
                            _transactionResponseFromVerve.value =
                                Resource.success(transResponse)
                        }
                        error?.let { throwable ->
                            Timber.d("ERROR_FROM_VP%s", throwable.localizedMessage)
                            _transactionResponseFromVerve.value =
                                if (throwable is SocketTimeoutException) Resource.timeOut(null) else Resource.error(
                                    null
                                )
                        }
                    }
            )
        }
    }

    // FOR QR
    fun setQrTransactionResponse(qrTransactionResponse: QrTransactionResponseModel) {
        _qrTransactionResponseFromWebView.value = qrTransactionResponse
    }

    fun showReceiptDialogForQrPayment() {
        _showQrPrintDialog.value = Event(
            Gson().toJson(qrTransactionResponseFromWebView.value)
        )
    }

    private fun saveTransIDAndAmountResponse(context: Context, transIDAndAmount: CheckOutResponse) {
        EncryptedPrefsUtils.putString(context, TRANS_ID_AND_AMOUNT, gson.toJson(transIDAndAmount))
    }

    fun setIsVerveCard(data: Boolean) {
        _isVerveCard.postValue(data)
    }

    fun clear() {
        _transactionResponse.postValue(Resource.error(null))
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}