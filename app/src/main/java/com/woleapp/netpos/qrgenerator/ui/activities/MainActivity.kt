package com.woleapp.netpos.qrgenerator.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.adapter.paging.SearchMerchantPagingAdapter
import com.woleapp.netpos.qrgenerator.databinding.ActivityMainBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterPasswordBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutReprintPasswordPrefTextBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.ui.fragments.RequestPaymentFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.SendWithTallyNumberFragment
import com.woleapp.netpos.qrgenerator.ui.fragments.SendWithTallyQrFragment
import com.woleapp.netpos.qrgenerator.utils.PIN_PASSWORD
import com.woleapp.netpos.qrgenerator.viewmodels.MerchantViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.reactivex.disposables.CompositeDisposable
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
//    private val binding get() = _binding
    private val generateQrViewModel by viewModels<QRViewModel>()
    private val fetchMerchantViewModel by viewModels<QRViewModel>()
    private lateinit var searchedMerchantAdapter: SearchMerchantPagingAdapter
    private val merchantViewModel by viewModels<MerchantViewModel>()
    private val mDisposable = CompositeDisposable()
    private lateinit var endLat: GenerateQRResponse
    private lateinit var inputPasswordDialog: AlertDialog
    private lateinit var passwordDialogBinding: LayoutEnterPasswordBinding
    private lateinit var passwordSetDialog: AlertDialog
    private lateinit var passwordSetBinding: LayoutReprintPasswordPrefTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_QRGenerator)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        generateQrViewModel.getCardSchemes()
//        generateQrViewModel.getCardBanks()
//        fetchMerchantViewModel.getAllMerchant()



        val sendWithTallyQrFragment = SendWithTallyQrFragment()
        binding.scanWithTallyQr.setOnClickListener {
            if (sendWithTallyQrFragment.isAdded) {
                return@setOnClickListener
            }
            navigateToDestination(sendWithTallyQrFragment)
        }

        val sendWithTallyNumber = SendWithTallyNumberFragment()
        binding.sendWithTallyNumber.setOnClickListener {
            if (sendWithTallyNumber.isAdded) {
                return@setOnClickListener
            }
            navigateToDestination(sendWithTallyNumber)
        }

        val requestPayment = RequestPaymentFragment()
        binding.requestPayment.setOnClickListener {
            if (requestPayment.isAdded) {
                return@setOnClickListener
            }
            navigateToDestination(requestPayment)
        }

        passwordDialogBinding =
            LayoutEnterPasswordBinding.inflate(LayoutInflater.from(this), null, false)
                .apply {
                    lifecycleOwner = this@MainActivity
                    executePendingBindings()
                }
        passwordSetBinding = LayoutReprintPasswordPrefTextBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        ).apply {
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }

        passwordSetBinding.save.setOnClickListener {
            if (passwordSetBinding.reprintPasswordEdittext.text.toString().isEmpty()) {
                Toast.makeText(this, "PIN cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (passwordSetBinding.reprintPasswordEdittext.text.toString() != passwordSetBinding.confirmReprintPasswordEdittext.text.toString()) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            Prefs.putString(
                PIN_PASSWORD,
                passwordSetBinding.reprintPasswordEdittext.text.toString()
            )
            sharedPrefsData()
            passwordSetBinding.confirmReprintPasswordEdittext.setText("")
            passwordSetBinding.reprintPasswordEdittext.setText("")
            Toast.makeText(this, "Password Set", Toast.LENGTH_SHORT).show()
            passwordSetDialog.cancel()
        }
        passwordDialogBinding.proceed.setOnClickListener {
            if (passwordDialogBinding.passwordEdittext.text.toString() == Prefs.getString(
                    PIN_PASSWORD,
                    ""
                )
            ) {
                inputPasswordDialog.dismiss()
            } else {
                Toast.makeText(this, "Incorrect pin", Toast.LENGTH_SHORT).show()
            }
        }

        passwordDialogBinding.reset.setOnClickListener {
            passwordSetDialog.show()
            inputPasswordDialog.dismiss()
        }
        passwordSetDialog = AlertDialog.Builder(this)
            .setView(passwordSetBinding.root)
            .setCancelable(false)
            .create()
        inputPasswordDialog = AlertDialog.Builder(this)
            .setView(passwordDialogBinding.root)
            .setCancelable(false)
            .create()

        if (restorePrefData()) {
            inputPasswordDialog.dismiss()
            inputPasswordDialog.show()
        } else {
            passwordSetDialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun navigateToDestination(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.mainActivityfragmentContainerView,
            fragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun restorePrefData(): Boolean {
        val pref: SharedPreferences? = application?.getSharedPreferences(
            "MyPref",
            AppCompatActivity.MODE_PRIVATE
        )
        val isIntroActivitySeenBefore: Boolean =
            pref?.getBoolean("IsIntroActivityOpened", false) == true
        return isIntroActivitySeenBefore
    }

    private fun sharedPrefsData() {
        val pref: SharedPreferences? = application?.getSharedPreferences(
            "MyPref",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean("IsIntroActivityOpened", true)
        editor?.apply()
    }

}