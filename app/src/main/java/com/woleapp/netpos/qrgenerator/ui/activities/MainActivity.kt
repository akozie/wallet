package com.woleapp.netpos.qrgenerator.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.*
import com.woleapp.netpos.qrgenerator.model.LoginRequest
import com.woleapp.netpos.qrgenerator.model.checkout.CheckOutModel
import com.woleapp.netpos.qrgenerator.model.login.UserEntity
import com.woleapp.netpos.qrgenerator.model.login.UserViewModel
import com.woleapp.netpos.qrgenerator.model.pay.ConnectionData
import com.woleapp.netpos.qrgenerator.utils.*
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.observeServerResponseActivity
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
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
    private val viewModel by viewModels<QRViewModel>()
    private lateinit var inputPasswordDialog: AlertDialog
    private lateinit var passwordDialogBinding: LayoutEnterPasswordBinding
    private lateinit var enterOTPDialog: AlertDialog
    private lateinit var enterOTPBinding: LayoutEnterOtpBinding
    private lateinit var loader: android.app.AlertDialog
    private lateinit var token: String
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var setYourPasswordDialog: AlertDialog
    private lateinit var setYourPasswordBinding: LayoutSetPasswordPrefBinding
    private lateinit var loginDialog: AlertDialog
    private lateinit var loginBinding: LayoutReenterPasswordToContinueBinding
    private lateinit var email: String
    private lateinit var fullName: String
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var loginPassword: String
    private lateinit var loginPasswordValue: String
    private var newSignIn = 0
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var dialogIsShowing = 0
    private lateinit var timer: CountDownTimer
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var timerCount = 0
    private val delayMillis = 1000L // 1 second delay
    private lateinit var passwordView: TextInputEditText
    private lateinit var loginButton: Button

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

        handler = Handler()

        navController = findNavController(R.id.mainActivityfragmentContainerView)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )
       // newFunction()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)

        loginBinding = LayoutReenterPasswordToContinueBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        ).apply {
            lifecycleOwner = this@MainActivity
            executePendingBindings()
        }

        loginDialog = AlertDialog.Builder(this)
            .setView(loginBinding.root)
            .setCancelable(false)
            .create()

        Singletons().getCurrentlyLoggedInUser(this)?.let {
            email = it.email.toString()
            fullName = it.fullname.toString()
        }

        initSignInViews()//initialize login views

        reEnterPasswordSpannableText()

        signOutSpannableText()


        viewModel.loginMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                loginButton.isEnabled = true
            }
        }
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
                R.id.webViewFragment2 -> {
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

        loader = alertDialog(this)

        binding.appBarDashboard.contentDashboard.signOut.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        // Continue with delete operation
                        val logoutRequest = JsonObject()
                            .apply {
                                addProperty("email", email)
                            }
                        viewModel.logout(logoutRequest)
                        observeServerResponseActivity(
                            this,
                            this,
                            viewModel.logoutResponse,
                            null,
                            supportFragmentManager
                        ) {
                            newSignIn = 0
                            EncryptedPrefsUtils.remove(this, PREF_USER)
                            EncryptedPrefsUtils.remove(this, LOGIN_PASSWORD_VALUE)
                            startActivity(
                                Intent(this, AuthenticationActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )
                        }
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show()
        }

        loginBinding.signOut.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        // Continue with delete operation
                        val logoutRequest = JsonObject()
                            .apply {
                                addProperty("email", email)
                            }
                        viewModel.logout(logoutRequest)
                        observeServerResponseActivity(
                            this,
                            this,
                            viewModel.logoutResponse,
                            null,
                            supportFragmentManager
                        ) {
                            newSignIn = 0
                            EncryptedPrefsUtils.remove(this, PREF_USER)
                            EncryptedPrefsUtils.remove(this, LOGIN_PASSWORD_VALUE)
                            startActivity(
                                Intent(this, AuthenticationActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )
                        }
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show()
        }


        val header = Singletons().getTallyUserToken(this)!!
        token = "Bearer $header"

        binding.appBarDashboard.contentDashboard.setUpAccount.setOnClickListener {
            navController.navigate(R.id.verificationFragment)
        }
        passwordDialogBinding =
            LayoutEnterPasswordBinding.inflate(LayoutInflater.from(this), null, false)
                .apply {
                    lifecycleOwner = this@MainActivity
                    executePendingBindings()
                }

        passwordDialogBinding.signOut.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(
                    android.R.string.yes,
                    DialogInterface.OnClickListener { _, _ ->
                        // Continue with delete operation
                        val logoutRequest = JsonObject()
                            .apply {
                                addProperty("email", email)
                            }
                        viewModel.logout(logoutRequest)
                        observeServerResponseActivity(
                            this,
                            this,
                            viewModel.logoutResponse,
                            null,
                            supportFragmentManager
                        ) {
                            newSignIn = 0
                            EncryptedPrefsUtils.remove(this, PREF_USER)
                            EncryptedPrefsUtils.remove(this, LOGIN_PASSWORD_VALUE)
                            startActivity(
                                Intent(this, AuthenticationActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )
                        }
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show()
        }

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


        drawerLayout.closeDrawers()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        loginPassword = Singletons().getLoginPassword(this).toString()
        loginPasswordValue = Singletons().getLoginPasswordValue(this).toString()


        passwordDialogBinding.proceed.setOnClickListener {
            val enterPin = passwordDialogBinding.passwordEdittext.text.toString().trim()
            if (enterPin == loginPassword) {
                dialogIsShowing = 0
                inputPasswordDialog.dismiss()
                passwordDialogBinding.passwordEdittext.setText("")
                cancelTimer()
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
            }
        }

        if (!isDestroyed && loginPasswordValue.isEmpty()) {
            inputPasswordDialog.show()
        }
        if (!isDestroyed && loginPasswordValue == "0") {
            inputPasswordDialog.dismiss()
        }


        qrViewModel.fetchWalletMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                if (message == "Expired token") {
                    loginDialog.show()
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.generateQrMessage.observe(this) {
            it.getContentIfNotHandled()?.let { message ->
                if (message == "Expired token") {
                    loginDialog.show()
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun signOutSpannableText() {
        val firstSignOutText = "Not $fullName?"
        val secondSignOutText = " Logout"

        val firstSpannable = SpannableString(firstSignOutText)
        val secondSpannable = SpannableString(secondSignOutText)

        // Apply different styles and spans
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))
        //  val boldSpan = StyleSpan(Typeface.BOLD)

        secondSpannable.setSpan(
            colorSpan,
            1,
            secondSignOutText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Concatenate spannable strings using SpannableStringBuilder
        val stringBuilder = SpannableStringBuilder()
        stringBuilder.append(firstSpannable)
        stringBuilder.append(secondSpannable)

        loginBinding.signOut.text = stringBuilder
    }

    private fun reEnterPasswordSpannableText() {
        val firstText = "Hi, ${fullName},"
        val secondText = " if this is still you, enter your password to continue."
        val firstSpannable = SpannableString(firstText)
        val secondSpannable = SpannableString(secondText)

        // Apply different styles and spans
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary))
        //  val boldSpan = StyleSpan(Typeface.BOLD)

        firstSpannable.setSpan(
            colorSpan,
            4,
            firstText.length - 1,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Concatenate spannable strings using SpannableStringBuilder
        val stringBuilder = SpannableStringBuilder()
        stringBuilder.append(firstSpannable)
        stringBuilder.append(secondSpannable)

        loginBinding.passwordText.text = stringBuilder
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
                        Toast.makeText(
                            this,
                            "PIN mismatch, please contact admin if you forgot PIN",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "Transaction PIN saved", Toast.LENGTH_SHORT).show()
                        userViewModel.insertUser(UserEntity(email, password))
                        setYourPasswordDialog.dismiss()
                    }
                }
            } else {
                val pin = it.data.pin
                inputPasswordDialog.show()
                dialogIsShowing = 1
                passwordDialogBinding.proceed.setOnClickListener {
                    val enterPin = passwordDialogBinding.passwordEdittext.text.toString().trim()
                    if (enterPin == pin) {
                        EncryptedPrefsUtils.putString(
                            this,
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
        super.onResume()
        stopRunnable()
    }

    override fun onPause() {
        super.onPause()
        newSignIn = 1
        loginPasswordValue = "1"
        timerCount = 0
        startTimer()
    }


    override fun onRestart() {
        super.onRestart()
        stopRunnable()
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

    private fun cancelTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    fun startTimer(time: Long, start: Boolean) {
        if (start) {
            timer = object : CountDownTimer(time, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Toast.makeText(this@MainActivity, "$millisUntilFinished", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFinish() {}
            }.start()
        } else {
            timer.cancel()
        }
    }


    fun startRunnable() {
        // you could just run it to get it going, since it posts itself
        runnable.run()
    }

    fun stopRunnable() {
        // remove this specific instance from the message queue
        handler.removeCallbacksAndMessages(null)
    }


    private fun startTimer() {
        // Increment the timerCount
        timerCount++
        newSignIn = 0
        EncryptedPrefsUtils.remove(this, LOGIN_PASSWORD_VALUE)

        // Perform actions here based on the timerCount value
        // For example, you can update a TextView with the timerCount value
        // textView.text = "Timer: $timerCount seconds"
        //Toast.makeText(this, "$timerCount", Toast.LENGTH_SHORT).show()
        // Check if you want to stop the timer after a certain duration
        val maxTimerCount = 120
        if (timerCount >= maxTimerCount) {
            // Stop the timer and perform any final actions
            // For example, you can display a message or perform other tasks
            // textView.text = "Timer has completed!"
            inputPasswordDialog.show()
            return
        }

        // Call postDelayed to schedule the next timer iteration
        handler.postDelayed({
            startTimer()
        }, delayMillis)
    }

    private fun initSignInViews() {
        with(loginBinding) {
            passwordView = signInPassword
            loginButton = signInButton
        }
    }

    private fun login() {
        when {
            passwordView.text.toString().isEmpty() -> {
                loginBinding.fragmentEnterPassword.error =
                    getString(R.string.all_please_enter_password)
                loginBinding.fragmentEnterPassword.errorIconDrawable = null
            }
            else -> {
                if (validateSignUpFieldsOnTextChange()) {
                    signIn()
                }
            }
        }
    }

    private fun validateSignUpFieldsOnTextChange(): Boolean {
        var isValidated = true

        passwordView.doOnTextChanged { _, _, _, _ ->
            when {
                passwordView.text.toString().trim().isEmpty() -> {
                    loginBinding.fragmentEnterPassword.error =
                        getString(R.string.all_please_enter_password)
                    isValidated = false
                }
                passwordView.text.toString().trim().isEmpty() -> {
                    loginBinding.fragmentEnterPassword.error = ""
                    isValidated = true
                }
                else -> {
                    loginBinding.fragmentEnterPassword.error = null
                    isValidated = true
                }
            }
        }
        return isValidated
    }

    private fun signIn() {
        val loginUser = LoginRequest(
            password = passwordView.text.toString().trim(),
            email = email
        )
        viewModel.login(
            this,
            loginUser
        )
        observeServerResponseActivity(
            this,
            this,
            viewModel.loginResponse,
            loader,
            supportFragmentManager
        ) {
            EncryptedPrefsUtils.putString(this, LOGIN_PASSWORD, passwordView.text.toString().trim())
            EncryptedPrefsUtils.putString(this, LOGIN_PASSWORD_VALUE, "0")
            loader.dismiss()
            loginDialog.dismiss()
        }
    }

    fun newFunction(){
        val gson = Gson()
        val connectionData = ConnectionData(
            ipAddress = "196.6.103.10",
            ipPort = 55533,
            isSSL = true
        )
        val jsonModel = gson.toJson(connectionData)
        val dataEncryption = DataEncryption()
        val encryptedData = dataEncryption.encryptData(jsonModel)
        Log.d("ENCRYPTEDDATA", encryptedData.toString())

//        if (encryptedData != null){
//            val encryptedData = dataEncryption.decryptData(encryptedData)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        newSignIn = 1
        loginPasswordValue = "1"
        stopRunnable()
    }
}