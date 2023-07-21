package com.woleapp.netpos.qrgenerator.app

import android.app.Application
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatDelegate
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TallyApp : Application(){

    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }
}
