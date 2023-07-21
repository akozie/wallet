package com.woleapp.netpos.qrgenerator.model.login

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single

@Dao
interface EmailDao {
//    @Query("SELECT * FROM email_table WHERE email = :email")
//    fun getEmail(email: String): Single<EmailEntity?>

//    @Query("SELECT * FROM email_table")
//    fun getEmail(): Single<EmailEntity?>
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    fun insertEmail(emailEntity: EmailEntity): Single<Long>
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(userEntity: UserEntity): Single<Long>

    @Query("SELECT pin FROM user_table WHERE email = :email")
    fun getPinByEmail(email: String): Single<String?>

    @Query("SELECT * FROM user_table WHERE email = :email")
    fun getUserEmail(email: String): Single<UserEntity?>

    @Update
    fun updatePin(userEntity: UserEntity): Single<Int>
}
