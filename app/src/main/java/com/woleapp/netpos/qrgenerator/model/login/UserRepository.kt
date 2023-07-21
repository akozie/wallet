package com.woleapp.netpos.qrgenerator.model.login

import androidx.lifecycle.LiveData
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val emailDao: EmailDao,
    private val userDao: UserDao

){

//    fun saveEmail(emailEntity: EmailEntity): Single<Long> = emailDao.insertEmail(emailEntity)
//    fun getEmail(): Single<EmailEntity?> = emailDao.getEmail()

    fun insertUser(userEntity: UserEntity): Single<Long> = userDao.insertUser(userEntity)
    fun getPinByEmail(email: String): Single<String?> = userDao.getPinByEmail(email)
    fun getUserEmail(email: String): Single<UserEntity?> = userDao.getUserEmail(email)
    fun updatePin(userEntity: UserEntity): Single<Int> = userDao.updatePin(userEntity)
}