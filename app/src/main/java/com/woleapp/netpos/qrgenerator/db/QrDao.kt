package com.woleapp.netpos.qrgenerator.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.woleapp.netpos.qrgenerator.model.DomainQREntity
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse

@Dao
interface QrDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQrCode(generateQRResponse: DomainQREntity)

    @Query("SELECT qrData FROM qr WHERE userId = :userQrCodeID")
    fun getUserQrCodes(userQrCodeID:String): List<GenerateQRResponse>
}