package com.woleapp.netpos.qrgenerator.ui.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.woleapp.netpos.qrgenerator.ui.model.GenerateQRResponse
import io.reactivex.Single

@Dao
interface QrDao {

    @Insert
    fun insertQrCode(generateQRResponse: List<GenerateQRResponse>)

//    @Query("SELECT * FROM transactionresponse WHERE terminalId=:terminalId ORDER BY id DESC")
//    fun getTransactions(terminalId: String): LiveData<List<TransactionResponse>>

//    @Query("SELECT * FROM transactionresponse WHERE transactionTimeInMillis >= :beginningOfDay and transactionTimeInMillis <= :endOfDay and terminalId=:terminalId")
//    fun getEndOfDayTransaction(
//        beginningOfDay: Long,
//        endOfDay: Long,
//        terminalId: String
//    ): LiveData<List<TransactionResponse>>

    @Query("SELECT * FROM qr")
    fun getQrCodes(): LiveData<List<GenerateQRResponse>>


}