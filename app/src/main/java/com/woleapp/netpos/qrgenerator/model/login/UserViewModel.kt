package com.woleapp.netpos.qrgenerator.model.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woleapp.netpos.qrgenerator.model.wallet.WithdrawalResponse
import com.woleapp.netpos.qrgenerator.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val compositeDisposable: CompositeDisposable
): ViewModel(){

    private lateinit var emailEntity: Single<EmailEntity?>

    private var _insertUserResponse: MutableLiveData<Single<Long>> =
        MutableLiveData()
    val insertUserResponse: LiveData<Single<Long>> get() = _insertUserResponse

    private var _getUserEmailResponse: MutableLiveData<Resource<UserEntity?>> =
        MutableLiveData()
    val getUserEmailResponse: LiveData<Resource<UserEntity?>> get() = _getUserEmailResponse

    private var _updatePinResponse: MutableLiveData<Resource<Int>> =
        MutableLiveData()
    val updatePinResponse: LiveData<Resource<Int>> get() = _updatePinResponse

    private var _getPinByEmailResponse: MutableLiveData<Single<String?>> =
        MutableLiveData()
    val getPinByEmailResponse: LiveData<Single<String?>> get() = _getPinByEmailResponse

//    fun saveEmail(emailEntity: EmailEntity){
//        compositeDisposable.add(
//            userRepository.saveEmail(emailEntity)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {data, error ->
//                    data?.let {
//                        Log.d("NEWUSER", it.toString())
//                    }
//                    error?.let {
//                        Log.d("NONEWUSER", it.toString())
//                    }
//                }
//        )
//    }
//
//    fun getEmail() {
//        compositeDisposable.add(
//            userRepository.getEmail()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { data, error ->
//                    data?.let {
//                  //      emailEntity = it
//                        Log.d("ALLEMAIL", it.toString())
//                    }
//                    error?.let {
//                        Log.d("NOEMAILLIST", it.toString())
//                    }
//                }
//        )
//       // return emailEntity
//    }

    fun insertUser(userEntity: UserEntity){
        compositeDisposable.add(
            userRepository.insertUser(userEntity)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {data, error ->
                    data?.let {
                        _insertUserResponse.postValue(Single.just(it))
                        Log.d("NEWUSER", it.toString())
                    }
                    error?.let {
                        _insertUserResponse.postValue(Single.error(it))
                        Log.d("NONEWUSER", it.toString())
                    }
                }
        )
    }

    fun getPinByEmail(email:String){
        compositeDisposable.add(
            userRepository.getPinByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {data, error ->
                    data?.let {
                        _getPinByEmailResponse.postValue(Single.just(it))
                    }
                    error?.let {
                        _getPinByEmailResponse.postValue(Single.error(it))
                    }
                }
        )
    }

    fun getUserEmail(email: String){
        compositeDisposable.add(
            userRepository.getUserEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {data, error ->
                    data?.let {
                     _getUserEmailResponse.postValue(Resource.success(it))
                    }
                    error?.let {
                        _getUserEmailResponse.postValue(Resource.error(null))
                    }
                }
        )
    }

    fun updatePin(userEntity: UserEntity){
        compositeDisposable.add(
            userRepository.updatePin(userEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {data, error ->
                    data?.let {
                        _updatePinResponse.postValue(Resource.success(it))
                        Log.d("UPDATEPIN", it.toString())
                    }
                    error?.let {
                        _updatePinResponse.postValue(Resource.error(null))
                    }
                }
        )
    }
}