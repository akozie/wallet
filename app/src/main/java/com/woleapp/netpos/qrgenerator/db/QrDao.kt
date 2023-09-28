package com.woleapp.netpos.qrgenerator.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.woleapp.netpos.qrgenerator.model.DomainQREntity
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import io.reactivex.Single

@Dao
interface QrDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQrCode(generateQRResponse: DomainQREntity)

    @Query("SELECT qrData FROM qr WHERE userId = :userQrCodeID")
    fun getUserQrCodes(userQrCodeID:String): List<GenerateQRResponse>

    @Query("SELECT * FROM qr WHERE userId = :userQrCodeID")
    fun getAllQrCodes(userQrCodeID:String): Single<List<DomainQREntity?>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertQrTransaction(qrTransactionResponseModel: QrTransactionResponseModel) : Single<Long>
}