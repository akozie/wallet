package com.woleapp.netpos.qrgenerator.ui.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.woleapp.netpos.qrgenerator.ui.model.GenerateQRResponse

@Database(entities = [GenerateQRResponse::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun qrDao(): QrDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabaseInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "tally-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}
