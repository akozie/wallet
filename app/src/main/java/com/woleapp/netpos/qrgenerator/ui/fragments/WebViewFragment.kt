package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentShowQrBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentWebViewBinding
import com.woleapp.netpos.qrgenerator.di.customDependencies.JavaScriptInterface
import com.woleapp.netpos.qrgenerator.di.customDependencies.WebViewCallBack
import com.woleapp.netpos.qrgenerator.utils.WEB_VIEW_JAVASCRIPT_TAG
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WebViewFragment : Fragment() {

    private lateinit var _binding: FragmentWebViewBinding
    private val binding get() = _binding
    private lateinit var webView: WebView
    private lateinit var webSettings: WebSettings
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var javaScriptInterface: JavaScriptInterface
    @Inject
    lateinit var customWebViewClient: WebViewCallBack


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)

        // Handle Back Press
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        )

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrViewModel.payResponse.observe(viewLifecycleOwner) { response ->
            response.data?.let {
                javaScriptInterface = JavaScriptInterface(
                    requireActivity().supportFragmentManager,
                    it.TermUrl,
                    it.MD,
                    it.PaReq,
                    it.ACSUrl,
                    it.transId
                )
                webView = binding.webView
                setUpWebView(webView)
            }
        }
    }

    private fun setUpWebView(webView: WebView) {
        webSettings = webView.settings
        webSettings.apply {
            javaScriptEnabled = true
        }
        webView.apply {
            webViewClient = customWebViewClient
            webChromeClient = WebChromeClient()
            addJavascriptInterface(javaScriptInterface, WEB_VIEW_JAVASCRIPT_TAG)
            loadUrl("file:///android_asset/3ds_pay.html")
        }
    }

}