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
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentShowQrBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentWebViewBinding
import com.woleapp.netpos.qrgenerator.di.customDependencies.WebViewCallBack
import com.woleapp.netpos.qrgenerator.utils.WEB_VIEW_JAVASCRIPT_TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WebViewFragment : Fragment() {

    private lateinit var _binding: FragmentWebViewBinding
    private val binding get() = _binding
    private lateinit var wbView: WebView
    private lateinit var webSettings: WebSettings

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
                    if (wbView != null) {
                        if (wbView.canGoBack()) {
                            wbView.goBack()
                        } else {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
            }
        )

        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wbView = binding.webView
    }

    private fun setUpWebView(webView: WebView) {
        webSettings = webView.settings
        webSettings.apply {
            javaScriptEnabled = true
        }
        webView.apply {
            webViewClient = customWebViewClient
            webChromeClient = WebChromeClient()
          //  addJavascriptInterface(javaScriptInterface, WEB_VIEW_JAVASCRIPT_TAG)
            loadUrl("file:///android_asset/3ds_pay.html")
        }
    }

}