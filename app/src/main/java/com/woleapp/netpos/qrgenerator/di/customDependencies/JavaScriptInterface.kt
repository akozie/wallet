package com.woleapp.netpos.qrgenerator.di.customDependencies

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.ui.dialog.ResponseModal
import com.woleapp.netpos.qrgenerator.utils.QR_TRANSACTION_RESULT_BUNDLE_KEY
import com.woleapp.netpos.qrgenerator.utils.QR_TRANSACTION_RESULT_REQUEST_KEY
import com.woleapp.netpos.qrgenerator.utils.STRING_QR_RESPONSE_MODAL_DIALOG_TAG
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject


class JavaScriptInterface(
    private val fragmentManager: FragmentManager,
    private val termUrl: String,
    private val md: String,
    private val cReq: String,
    private val acsUrl: String,
    private val transId: String
) {
    private val webViewBaseUrl =
        BuildConfig.STRING_WEB_VIEW_BASE_URL + BuildConfig.STRING_CHECKOUT_MERCHANT_ID + "/"

    @JavascriptInterface
    fun sendValueToWebView() =
        "$termUrl<======>$md<======>$cReq<======>$acsUrl<======>$transId<======>$webViewBaseUrl"

    @JavascriptInterface
    fun webViewCallback(webViewResponse: String) {
        Log.d("CALLBACKSTRING", webViewResponse)
        val responseFromWebView = Gson().fromJson(
            webViewResponse,
            QrTransactionResponseModel::class.java
        )
        fragmentManager.setFragmentResult(
            QR_TRANSACTION_RESULT_REQUEST_KEY,
            bundleOf(QR_TRANSACTION_RESULT_BUNDLE_KEY to responseFromWebView)
        )
        fragmentManager.popBackStack()
        val responseModal = ResponseModal()
        responseModal.show(fragmentManager, STRING_QR_RESPONSE_MODAL_DIALOG_TAG)
    }
}
