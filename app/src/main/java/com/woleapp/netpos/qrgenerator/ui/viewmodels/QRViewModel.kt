package com.woleapp.netpos.qrgenerator.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.ui.db.AppDatabase
import com.woleapp.netpos.qrgenerator.ui.model.*
import com.woleapp.netpos.qrgenerator.ui.network.QRRepository
import com.woleapp.netpos.qrgenerator.ui.utils.Event
import com.woleapp.netpos.qrgenerator.ui.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val qrRepository: QRRepository
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private var _loginResponse: MutableLiveData<Resource<LoginResponse>> =
        MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

    private var _registerResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()
    val registerResponse: LiveData<Resource<GeneralResponse>> get() = _registerResponse

    private var _generateQrResponse: MutableLiveData<Resource<GenerateQRResponse>> =
        MutableLiveData()
    val generateQrResponse: LiveData<Resource<GenerateQRResponse>> get() = _generateQrResponse

    private var _cardSchemeResponse: MutableLiveData<Resource<CardSchemeResponse>> =
        MutableLiveData()
    val cardSchemeResponse: LiveData<Resource<CardSchemeResponse>> get() = _cardSchemeResponse

    private var _issuingBankResponse: MutableLiveData<Resource<BankCardResponse>> =
        MutableLiveData()
    val issuingBankResponse: LiveData<Resource<BankCardResponse>> get() = _issuingBankResponse

    private val _generateQrMessage = MutableLiveData<Event<String>>()
    val generateQrMessage: LiveData<Event<String>>
        get() = _generateQrMessage

    private val _registerMessage = MutableLiveData<Event<String>>()
    val registerMessage: LiveData<Event<String>>
        get() = _registerMessage

    private val _loginMessage = MutableLiveData<Event<String>>()
    val loginMessage: LiveData<Event<String>>
        get() = _loginMessage

    val cardSchemes = arrayListOf<Row>()

    val issuingBank = arrayListOf<RowX>()

    fun login(loginRequest: LoginRequest) {
        _loginResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.login(loginRequest)
                .flatMap {
                    Timber.e(it.toString())
                    if (!it.success) {
                        throw Exception("Login Failed, Check Credentials")
                    }
                    val userToken = it.token
//                    val stormId: String =
//                        JWTHelper.getStormId(userToken) ?: throw Exception("Login Failed")
                  //  Prefs.putString(PREF_USER_TOKEN, userToken)
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
                    Timber.d("BUSINESSADDRESS------> ${user.fullname}")
                    Single.just(user)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        //_loginResponse.postValue(Resource.success(it))
                        _loginMessage.value = Event("Success")
                    }
                    error?.let {
                        _loginResponse.postValue(Resource.error(null))
                       // Timber.d("ERROR==${it.localizedMessage}")
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
                            //Timber.e("SHOWME--->$errorMessage")
                        }
                    }
                }
        )
    }

    fun register(registerRequest: RegisterRequest) {
        _registerResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.register(registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _registerResponse.postValue(Resource.success(it))
                        _registerMessage.value = Event("Success")
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
                            //Timber.e("SHOWME--->$errorMessage")
                        }
                    }
                }
        )
    }

    fun generateQR(qrModelRequest: QrModelRequest, context:Context) {
        _generateQrResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.generateQR(qrModelRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _generateQrResponse.postValue(Resource.success(it))
                        _generateQrMessage.value = Event("Success")
                        AppDatabase.getDatabaseInstance(context).qrDao()
                            .insertQrCode(arrayListOf(it))
                    }
                    error?.let {
                        _generateQrResponse.postValue(Resource.error(null))
                       // Timber.d("ERROR==${it.localizedMessage}")
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
                            //Timber.e("SHOWME--->$errorMessage")
                        }
                    }
                }
        )
    }

    fun getCardSchemes() {
        _cardSchemeResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.getCardSchemes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _cardSchemeResponse.postValue(Resource.success(it))
                      //  _message.value = Event("Success")
                        for (i in 0 until it.rows.size) {
                            it.rows[i].let { it1 -> cardSchemes.add(it1) }

                        }
                    }
                    error?.let {
                        _cardSchemeResponse.postValue(Resource.error(null))
                       // Timber.d("ERROR==${it.localizedMessage}")
//                        (it as? HttpException).let { httpException ->
//                            val errorMessage = httpException?.response()?.errorBody()?.string()
//                                ?: "{\"message\":\"Unexpected error\"}"
//                            _message.value = Event(
//                                try {
//                                    gson.fromJson(errorMessage, ErrorModel::class.java).message
//                                } catch (e: Exception) {
//                                    "Error"
//                                }
//                            )
//                            //Timber.e("SHOWME--->$errorMessage")
//                        }
                    }
                }
        )
    }

    fun getCardBanks() {
        _issuingBankResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.getCardBanks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _issuingBankResponse.postValue(Resource.success(it))
                   //     _message.value = Event("Success")
                        for (i in 0 until it.rows.size) {
                            it.rows[i].let { it1 -> issuingBank.add(it1) }
                        }
                    }
                    error?.let {
                        _issuingBankResponse.postValue(Resource.error(null))
                       // Timber.d("ERROR==${it.localizedMessage}")
//                        (it as? HttpException).let { httpException ->
//                            val errorMessage = httpException?.response()?.errorBody()?.string()
//                                ?: "{\"message\":\"Unexpected error\"}"
//                            _message.value = Event(
//                                try {
//                                    gson.fromJson(errorMessage, ErrorModel::class.java).message
//                                } catch (e: Exception) {
//                                    "Error"
//                                }
//                            )
//                            //Timber.e("SHOWME--->$errorMessage")
//                        }
                    }
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}