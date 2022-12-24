package com.woleapp.netpos.qrgenerator.ui.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

const val PREF_QR_PASSWORD = "qr_password"

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}