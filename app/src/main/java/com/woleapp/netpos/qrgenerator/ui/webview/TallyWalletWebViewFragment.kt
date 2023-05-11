package com.woleapp.netpos.qrgenerator.ui.webview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentTallyWalletWebViewBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentWebViewBinding
import com.woleapp.netpos.qrgenerator.di.customDependencies.JavaScriptInterface
import com.woleapp.netpos.qrgenerator.di.customDependencies.TallyWalletJavaScriptInterface
import com.woleapp.netpos.qrgenerator.di.customDependencies.WebViewCallBack
import com.woleapp.netpos.qrgenerator.utils.STRING_TAG_JAVASCRIPT_INTERFACE_TAG
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TallyWalletWebViewFragment : Fragment() {

    private lateinit var binding: FragmentTallyWalletWebViewBinding
    private lateinit var webView: WebView
    private lateinit var webSettings: WebSettings
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var javaScriptInterface: TallyWalletJavaScriptInterface

    @Inject
    lateinit var customWebViewClient: WebViewCallBack


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_tally_wallet_web_view, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = binding.tallyWebView
        qrViewModel.payResponse.observe(viewLifecycleOwner) { response ->
            response.data?.let {
                javaScriptInterface = TallyWalletJavaScriptInterface(
                    parentFragmentManager,
                    it.TermUrl,
                    it.MD,
                    it.PaReq,
                    it.ACSUrl,
                    it.transId,
                    it.redirectHtml
                )
            }
            setUpWebView(webView)
        }

    }

    private fun setUpWebView(webView: WebView) {
        webSettings = webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        webView.apply {
            webViewClient = customWebViewClient
            webChromeClient = WebChromeClient()
            addJavascriptInterface(javaScriptInterface, STRING_TAG_JAVASCRIPT_INTERFACE_TAG)
            loadUrl("file:///android_asset/3ds_pay.html")
        }
    }
}