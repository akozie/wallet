package com.woleapp.netpos.qrgenerator.ui.webview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentNeedCardWebViewBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentWebViewBinding
import com.woleapp.netpos.qrgenerator.di.customDependencies.JavaScriptInterface
import com.woleapp.netpos.qrgenerator.di.customDependencies.WebViewCallBack
import com.woleapp.netpos.qrgenerator.utils.STRING_TAG_JAVASCRIPT_INTERFACE_TAG
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NeedCardWebViewFragment : Fragment() {

    private lateinit var binding: FragmentNeedCardWebViewBinding
//    private val binding get() = _binding
    private lateinit var webView: WebView
    private lateinit var webSettings: WebSettings


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_need_card_web_view, container, false)
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
        webView = binding.webView
            setUpWebView(webView)
    }

    private fun setUpWebView(webView: WebView) {
        webSettings = webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        webView.apply {
            webChromeClient = WebChromeClient()
            loadUrl("https://oap.providusbank.com/accountopening/#/")
        }
    }
}