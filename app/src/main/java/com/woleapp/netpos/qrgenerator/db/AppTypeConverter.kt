package com.woleapp.netpos.qrgenerator.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse

class AppTypeConverter {
    @TypeConverter
    fun fromQrData(qrResponse: GenerateQRResponse):String{
        return Gson().toJson(qrResponse)
    }

    @TypeConverter
    fun toQrData(qrData:String):GenerateQRResponse{
        return Gson().fromJson(qrData, GenerateQRResponse::class.java)
    }
}