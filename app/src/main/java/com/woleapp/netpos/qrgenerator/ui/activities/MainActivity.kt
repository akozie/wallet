package com.woleapp.netpos.qrgenerator.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.*
import com.woleapp.netpos.qrgenerator.model.login.UserEntity
import com.woleapp.netpos.qrgenerator.model.login.UserViewModel
import com.woleapp.netpos.qrgenerator.model.wallet.request.ConfirmTransactionPin
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponseActivity
import com.woleapp.netpos.qrgenerator.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val qrViewModel by viewModels<WalletViewModel>()
    private lateinit var inputPasswordDialog: AlertDialog
    private lateinit var passwordDialogBinding: LayoutEnterPasswordBinding
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var loader: android.app.AlertDialog
    private lateinit var token: String
    private var count: Int = 0
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var setYourPasswordDialog: AlertDialog
    private lateinit var setYourPasswordBinding: LayoutSetPasswordPrefBinding
    private lateinit var email: String
    private val userViewModel by viewModels<UserViewModel>()
    //  private lateinit var result: UserEntity
    private lateinit var loginPassword: String
    private var newSignIn = 0

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    @Inject
    @Named("io-scheduler")
    lateinit var ioScheduler: Scheduler

    @Inject
    @Named("main-scheduler")
    lateinit var mainThreadScheduler: Scheduler
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_QRGenerator)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)


        navController = findNavController(R.id.mainActivityfragmentContainerView)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.transferFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.sendWithTallyNumberFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.addToBalanceFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.generateMoreQrFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.displayQrFragment2 -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.displayWalletQrFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.merchantDetailsFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.sendWithTallyNumberFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.sendWithTallyQrFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.sendWithTallyQrResultFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.withdrawalFragment -> {
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                }
                R.id.recentTransactionDetailsFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.transactionDetailsFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.transactionDetailsFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.qrDetailsFragment2 -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                R.id.verificationFragment -> {
                    setSupportActionBar(binding.appBarDashboard.dashboardActivityToolbar)
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.GONE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.GONE
                }
                else -> {
                    binding.appBarDashboard.dashboardActivityToolbar.setNavigationOnClickListener(
                        null
                    )
                    binding.appBarDashboard.contentDashboard.setUpAccount.visibility = View.VISIBLE
                    binding.appBarDashboard.contentDashboard.signOut.visibility = View.VISIBLE
                }
            }
        }

        loader = alertDialog(this, R.layout.layout_loading_dialog)
        qrViewModel.fetchWalletMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.appBarDashboard.contentDashboard.signOut.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        // Continue with delete operation
                        newSignIn = 0
                        Prefs.remove(PREF_USER)
                        startActivity(
                            Intent(this, AuthenticationActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show()
        }

        val header = Singletons().getTallyUserToken()!!
        token = "Bearer $header"

        Singletons().getCurrentlyLoggedInUser()?.email?.let {
            email = it
            Log.d("EMAIL", email)
        }

        binding.appBarDashboard.contentDashboard.setUpAccount.setOnClickListener {
            navController.navigate(R.id.verificationFragment)
        }
        passwordDialogBinding =
            LayoutEnterPasswordBinding.inflate(LayoutInflater.from(this), null, false)
                .apply {
                    lifecycleOwner = this@MainActivity
                    executePendingBindings()
                }




//        passwordDialogBinding.proceed.setOnClickListener {
//            loader.show()
//            val transactionPin = passwordDialogBinding.passwordEdittext.text.toString()
//            val newTransactionPin = ConfirmTransactionPin(
//                transaction_pin = transactionPin
//            )
//            observeServerResponseActivity(
//                this,
//                qrViewModel.confirmTransactionPin(
//                    "Bearer ${Singletons().getTallyUserToken()!!}",
//                    newTransactionPin
//                ),
//                loader,
//                compositeDisposable,
//                ioScheduler,
//                mainThreadScheduler,
//                supportFragmentManager
//            ) {
//                val confirmTransactionPinResponse = Prefs.getString(CONFIRM_PIN_RESPONSE, "")
//                if (confirmTransactionPinResponse == "true") {
//                    inputPasswordDialog.dismiss()
//                    passwordDialogBinding.passwordEdittext.setText("")
//                    return@observeServerResponseActivity
//                }
//                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
//
//            }
//
//        }

        enterOTPBinding = LayoutEnterOtpBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        ).apply {
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }
        enterOTPDialog = AlertDialog.Builder(this)
            .setView(enterOTPBinding.root)
            .setCancelable(false)
            .create()


        inputPasswordDialog = AlertDialog.Builder(this)
            .setView(passwordDialogBinding.root)
            .setCancelable(false)
            .create()

        setYourPasswordBinding = LayoutSetPasswordPrefBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        ).apply {
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }
        setYourPasswordDialog = AlertDialog.Builder(this)
            .setView(setYourPasswordBinding.root)
            .setCancelable(false)
            .create()

        //fetchWallet()

//        enterOTPBinding.proceed.setOnClickListener {
//            otp = enterOTPBinding.otpEdittext.text?.trim().toString()
//            if (otp.isEmpty()){
//                showToast("Please enter OTP")
//                return@setOnClickListener
//            }
//            verifyWalletOTP(token, otp)
//        }
//        enterOTPBinding.resendOtp.setOnClickListener {
//            verifyWalletAccount()
//        }
//        enterOTPBinding.closeDialog.setOnClickListener {
//            enterOTPDialog.dismiss()
//            verifyWalletOTP(token, "")
//        }

//        if (restorePrefData()) {
//            inputPasswordDialog.dismiss()
//            inputPasswordDialog.show()
//        } else {
//             passwordSetDialog.show()
//        }


        drawerLayout.closeDrawers()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


  //      findUserEmailInDB()

         loginPassword = Singletons().getLoginPassword()

        passwordDialogBinding.proceed.setOnClickListener {
            val enterPin = passwordDialogBinding.passwordEdittext.text.toString().trim()
            if (enterPin == loginPassword) {
                inputPasswordDialog.dismiss()
                passwordDialogBinding.passwordEdittext.setText("")
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findUserEmailInDB() {
        userViewModel.getUserEmail(email)
        userViewModel.getUserEmailResponse.observe(this) {
            if (it.data == null) {
                setYourPasswordDialog.show()
                setYourPasswordBinding.save.setOnClickListener {
                    val password =
                        setYourPasswordBinding.reprintPasswordEdittext.text.toString().trim()
                    val resetPassword =
                        setYourPasswordBinding.confirmReprintPasswordEdittext.text.toString().trim()
                    if (password != resetPassword) {
                        Toast.makeText(this, "PIN mismatch, please contact admin if you forgot PIN", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Transaction PIN saved", Toast.LENGTH_SHORT).show()
                        userViewModel.insertUser(UserEntity(email, password))
                        setYourPasswordDialog.dismiss()
                    }
                }
            } else {
                val pin = it.data.pin
                inputPasswordDialog.show()
                passwordDialogBinding.proceed.setOnClickListener {
                    val enterPin = passwordDialogBinding.passwordEdittext.text.toString().trim()
                    if (enterPin == pin) {
                        Prefs.putString(
                            PIN_PASSWORD,
                            pin
                        )
                        inputPasswordDialog.dismiss()
                    } else {
                        Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        if (newSignIn == 0){
            inputPasswordDialog.dismiss()
            newSignIn = 1
        }else{
            inputPasswordDialog.show()
        }
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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