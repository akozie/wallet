package com.woleapp.netpos.qrgenerator.app

import android.app.Application
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.worker.TokenUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TallyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //  DPrefs.initializeDPrefs(this)
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateRequest =
            PeriodicWorkRequestBuilder<TokenUpdateWorker>(10, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInitialDelay(2, TimeUnit.HOURS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "myWork",
            ExistingPeriodicWorkPolicy.REPLACE, updateRequest
        )


    }
}
