package com.woleapp.netpos.qrgenerator.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.utils.PREF_USER
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticationActivity @Inject constructor() : AppCompatActivity() {
    private val generateQrViewModel by viewModels<QRViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_QRGenerator)
        //installSplashScreen()
        super.onCreate(savedInstanceState)
        generateQrViewModel.getCardSchemes()
        generateQrViewModel.getCardBanks()

        if (Prefs.getString(PREF_USER, "").isNullOrEmpty()) {
            setContentView(R.layout.activity_authentication)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}