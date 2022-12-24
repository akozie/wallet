package com.woleapp.netpos.qrgenerator.ui.activities

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.ui.viewmodels.QRViewModel
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
        setContentView(R.layout.activity_authentication)
    }
}