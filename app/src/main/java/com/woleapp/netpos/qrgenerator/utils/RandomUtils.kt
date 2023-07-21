package com.woleapp.netpos.qrgenerator.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.Status
import com.woleapp.netpos.qrgenerator.model.wallet.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object RandomUtils {

    fun <T> observeServerResponseActivity(
        context: Context,
        lifecycle: LifecycleOwner,
        serverResponse: LiveData<Resource<T>>,
        loadingDialog: AlertDialog,
        fragmentManager: FragmentManager,
        successAction: () -> Unit,
    ) {
        serverResponse.observe(lifecycle) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    successAction()
                }
                Status.LOADING -> {
                    loadingDialog.show()
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    //   Toast.makeText(context, R.string.an_error_occurred, Toast.LENGTH_SHORT).show()
                }
                Status.TIMEOUT -> {
                    loadingDialog.dismiss()
                    Toast.makeText(context, R.string.timeOut, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun <T> observeServerResponseActivity(
        context: Context,
        serverResponse: Single<Resource<T>>,
        loadingDialog: AlertDialog,
        compositeDisposable: CompositeDisposable,
        ioScheduler: Scheduler,
        mainThreadSchedulers: Scheduler,
        fragmentManager: FragmentManager,
        successAction: () -> Unit,
    ) {
        compositeDisposable.add(
            serverResponse.subscribeOn(ioScheduler).observeOn(mainThreadSchedulers)
                .subscribe { data, error ->
                    data?.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                loadingDialog.dismiss()
                                if (
                                    it.data is ConfirmTransactionPinResponse
                                ) {
                                    successAction()
                                } else {
                                    Toast.makeText(context, R.string.an_error_occurred, Toast.LENGTH_SHORT).show()
                                }
                            }
                            Status.LOADING -> {
                                loadingDialog.show()
                            }
                            Status.ERROR -> {
                                loadingDialog.dismiss()
                                if (it.data is String && it.data.contains("false")) {
                                    Toast.makeText(context, "Wrong PIN", Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, "An error occurred, please try again", Toast.LENGTH_SHORT).show()
                                }
                            }
                            Status.TIMEOUT -> {
                                loadingDialog.dismiss()
                                Toast.makeText(context, R.string.timeOut, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    error?.let {
                        loadingDialog.dismiss()
                    }
                }
        )    }


    fun <T> Fragment.observeServerResponse(
        serverResponse: Single<Resource<T>>,
        loadingDialog: AlertDialog,
        compositeDisposable: CompositeDisposable,
        ioScheduler: Scheduler,
        mainThreadSchedulers: Scheduler,
        successAction: () -> Unit,
    ) {
        compositeDisposable.add(
            serverResponse.subscribeOn(ioScheduler).observeOn(mainThreadSchedulers)
                .subscribe { data, error ->
                    data?.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                loadingDialog.dismiss()
                                if (
                                    it.data is SendWithTallyNumberResponse ||
                                            it.data is TransactionReceiptResponse ||
                                            it.data is FetchQrTokenResponse
                                ) {
                                    successAction()
                                } else {
                                    showSnackBar(
                                        this.requireView(),
                                        getString(R.string.an_error_occurred),
                                    )
                                }
                            }
                            Status.LOADING -> {
                                loadingDialog.show()
                            }
                            Status.ERROR -> {
                                loadingDialog.dismiss()
                                if (it.data is String && it.data.contains("Recipient")) {
                                    showToast("Recipient is not a Tally user or account is not verified")
                                } else if (it.data is String && it.data.contains("User")) {
                                    showToast("User cannot send money to self")
                                } else if (it.data is String && it.data.contains("Insufficient")) {
                                    showToast("Insufficient Balance")
                                } else {
                                    showToast("Wrong Pin")
                                }
                            }
                            Status.TIMEOUT -> {
                                loadingDialog.dismiss()
                                showSnackBar(this.requireView(), getString(R.string.timeOut))
                            }
                        }
                    }
                    error?.let {
                        loadingDialog.dismiss()
                    }
                }
        )
    }


    fun <T> Fragment.observeServerResponse(
        serverResponse: LiveData<Resource<T>>,
        loadingDialog: AlertDialog,
        fragmentManager: FragmentManager,
        successAction: () -> Unit
    ) {
        serverResponse.observe(this.viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    successAction()
                }
                Status.LOADING -> {
                  //  Log.d("LOADING", "LOADINGRESULT")
                    loadingDialog.show()
                }
                Status.ERROR -> {
                    loadingDialog.cancel()
                    loadingDialog.dismiss()
                }
                Status.TIMEOUT -> {
                    loadingDialog.cancel()
                    loadingDialog.dismiss()
                }
            }
        }
    }

    fun <T> Fragment.observeServerResponseOnce(
        serverResponse: LiveData<Resource<T>>,
        loadingDialog: AlertDialog,
        fragmentManager: FragmentManager,
        successAction: () -> Unit
    ) {
        serverResponse.observe(this.viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    successAction()
                }
                Status.LOADING -> {
                    loadingDialog.show()
                }
                Status.ERROR -> {
                    loadingDialog.cancel()
                    loadingDialog.dismiss()
                }
                Status.TIMEOUT -> {
                    loadingDialog.cancel()
                    loadingDialog.dismiss()
                    showToast(getString(R.string.timeOut))
                }
            }
        }
        serverResponse.observeOnce(this.viewLifecycleOwner, null)
    }

    fun <T> Fragment.observeServerResponse(
        serverResponse: LiveData<Resource<T>>,
        loadingDialog: ProgressBar?,
        fragmentManager: FragmentManager,
        successAction: () -> Unit
    ) {
        serverResponse.observe(this.viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingDialog?.visibility = View.GONE
                    successAction()
                }
                Status.LOADING -> {
                    loadingDialog?.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    loadingDialog?.visibility = View.GONE
                }
                Status.TIMEOUT -> {
                    loadingDialog?.visibility = View.GONE
                    showToast(getString(R.string.timeOut))
                }
            }
        }
    }

    fun <T> Fragment.observeServerResponse(
        serverResponse: LiveData<Resource<T>>,
        fragmentManager: FragmentManager,
        successAction: () -> Unit
    ) {
        serverResponse.observe(this.viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    successAction()
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                }
                Status.TIMEOUT -> {
                    showToast(getString(R.string.timeOut))
                }
            }
        }
    }


    fun alertDialog(
        context: Context, layout: Int
    ): AlertDialog {
        val dialogView: View = LayoutInflater.from(context).inflate(layout, null)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(dialogView)

        return dialogBuilder.create()
    }

    fun alertDialog(
        context: Context
    ): AlertDialog {
        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.layout_custom_loading_dialog, null)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setView(dialogView)
        randomGeneratedTexts(dialogView.findViewById(R.id.custom_texts))
        return dialogBuilder.create()
    }

    fun getGUID() = UUID.randomUUID().toString().replace("-", "")

    fun stringToBase64(text: String): String {
        val data: ByteArray = text.toByteArray()
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun base64ToPlainText(base64String: String): String {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return String(decodedString)
    }

    fun closeSoftKeyboard(context: Context, activity: Activity) {
        activity.currentFocus?.let { view ->
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun customSpannableString(
        text: String,
        startIndex: Int,
        endIndex: Int,
        clickAction: () -> Unit,
    ): SpannableString {
        val spannedText = SpannableString(text)
        val styleSpan = StyleSpan(Typeface.BOLD)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickAction()
            }
        }
        if (startIndex < 0) throw IndexOutOfBoundsException("$startIndex must be at least 0")
        if (text.isEmpty()) throw IndexOutOfBoundsException("$text can't be empty")
        if (endIndex > text.length) throw IndexOutOfBoundsException("$endIndex can't be greater than the length of $text")
        spannedText.setSpan(styleSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannedText.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
        )
        return spannedText
    }


    fun createClientDataForNonVerveCard(
        transID: String, cardNumber: String, expiryDate: String, cvv: String
    ): String = "$transID:LIVE:$cardNumber:$expiryDate:$cvv::NGN:QR"

    fun createClientDataForVerveCard(
        qrModelRequest: QrModelRequest, pin: String, transID: String
    ): String {
        val verve = qrModelRequest.let {
            "$transID:LIVE:${it.card_number}:${it.card_expiry}:${it.card_cvv}:$pin:NGN:QR"
        }
        Log.d("VERVEEEE", verve)
        Prefs.putString(
            SAVE_CUSTOMER_DETAILS,
            "${qrModelRequest.fullname}===${qrModelRequest.email}"
        )
        return verve
    }

    fun Number.formatCurrency(currencySymbol: String = "\u20A6"): String {
        val format = DecimalFormat("#,###.00")
        return "$currencySymbol${format.format(this)}"
    }

//    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
//        observe(lifecycleOwner) { t ->
//            observer.onChanged(t)
//            removeObservers(lifecycleOwner)
//        }
//    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>?) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer?.onChanged(t)
                removeObserver(this)
            }
        })
    }

    fun validatePasswordMismatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun randomGeneratedTexts(textView: TextView) {
        Log.d("<=RANDOMGENERAEDTEXTS=>", "HERE")
        val countDownTimer = object : CountDownTimer(3000, 300) {
            override fun onTick(millisUntilFinished: Long) {
                // Not used in this example, but called every second during the countdown
            }

            override fun onFinish() {
                val random = Random()
                val randomTexts = arrayOf(
                    "Nigeria is one of the nine African countries that prints her own money",
                    "Largest Economy in Africa: Nigeria has the largest economy in Africa in terms of GDP (Gross Domestic Product)",
                    "To remain financially happy avoid comparing your earnings to that of others or try to spend the way they do",
                    "Planning and having a money budget help you spend your money judiciously.",
                    "Research shows that more people visit the ATM on Fridays compared with other days. Maybe because it’s the weekend!",
                    "It will take Bill Gates 218 years to spend all his money that’s if he spends \$1 million a day?",
                    "The 1st ATM in Nigeria was first installed by NCR in 1987",
                    "To facilitate an efficient payments system, the N100,and higher notes were introduced in between December 1999, and October 2005",
                    "Until 1991, N20 was the highest note/denomination in Nigeria",
                    "Naira was introduced on Monday, January 1, 1973, to replace the British pound as the official currency of Nigeria",
                    "Naira was coined by Chief Obafemi Awolowo when he was the Federal Commissioner for Finance?"
                ) // Add more text options as needed
                val index = random.nextInt(randomTexts.size)
                val randomText = randomTexts[index]
                Log.d("<=GENERAEDTEXTS=>", "HEREEEEE")

                textView.text = randomText
            }
        }
        countDownTimer.start()
    }

    fun Long.formatDate(): String? =
        SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date(this))

    fun internetCheck(c: Context): Boolean {
        val cmg =
            c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+
            cmg.getNetworkCapabilities(cmg.activeNetwork)?.let { networkCapabilities ->
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        } else {
            return cmg.activeNetworkInfo?.isConnectedOrConnecting == true
        }

        return false
    }

    fun isOnline(c: Context): Boolean {

        val connectivityMgr =
            c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val allNetworks: Array<Network> = connectivityMgr.allNetworks // added in API 21 (Lollipop)

        for (network in allNetworks) {
            val networkCapabilities = connectivityMgr!!.getNetworkCapabilities(network)
            return (networkCapabilities!!.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)))
        }

        return false
    }

    fun checkCardType(cardNumber: String, img: ImageView): Any {
        val visaPattern = "^4[0-9]{12}(?:[0-9]{3})?\$"
        val mastercardPattern = "^5[1-5][0-9]{14}\$"
        val amexPattern = "^3[47][0-9]{13}\$"
        val discoverPattern = "^6(?:011|5[0-9]{2})[0-9]{12}\$"
        val vervePattern = "^((506(0|1|2|3|4|5))|(650(0|1|2|3|4|5)))\\d{12}$"
        val anotherVervePattern = "^((506(0|1|2|3|4|5))|(650(0|1|2|3|4|5)))\\d{15}$"

//        return when {
//            cardNumber.matches(Regex(visaPattern)) -> "Visa"
//            cardNumber.matches(Regex(mastercardPattern)) -> "Mastercard"
//            cardNumber.matches(Regex(amexPattern)) -> "American Express"
//            cardNumber.matches(Regex(discoverPattern)) -> "Discover"
//            cardNumber.matches(Regex(vervePattern)) -> "Verve"
//            cardNumber.matches(Regex(anotherVervePattern)) -> "Verve"
//            else -> "Unknown"
//        }
        return when {
            cardNumber.matches(Regex(visaPattern)) -> img.setImageResource(R.drawable.visa)
            cardNumber.matches(Regex(mastercardPattern)) -> img.setImageResource(R.drawable.mastercard_logo)
            cardNumber.matches(Regex(amexPattern)) -> "American Express"
            cardNumber.matches(Regex(discoverPattern)) -> "Discover"
            cardNumber.matches(Regex(vervePattern)) -> img.setImageResource(R.drawable.verve)
            cardNumber.matches(Regex(anotherVervePattern)) -> img.setImageResource(R.drawable.verve)
            else -> "Unknown"
        }
    }
    fun checkCardScheme(
        cardNumber: String,
    ): String {
        var newResult : String = ""
        val visaPattern = "^4[0-9]{12}(?:[0-9]{3})?\$"
        val mastercardPattern = "^5[1-5][0-9]{14}\$"
        val amexPattern = "^3[47][0-9]{13}\$"
        val discoverPattern = "^6(?:011|5[0-9]{2})[0-9]{12}\$"
        val vervePattern = "^((506(0|1|2|3|4|5))|(650(0|1|2|3|4|5)))\\d{12}$"
        val anotherVervePattern = "^((506(0|1|2|3|4|5))|(650(0|1|2|3|4|5)))\\d{15}$"
         when {
            cardNumber.matches(Regex(visaPattern)) -> newResult = "Visa"
            cardNumber.matches(Regex(mastercardPattern)) -> newResult = "Mastercard"
            cardNumber.matches(Regex(amexPattern)) -> newResult = "American Express"
            cardNumber.matches(Regex(discoverPattern)) -> newResult = "Discover"
            cardNumber.matches(Regex(vervePattern)) -> newResult = "Verve"
            cardNumber.matches(Regex(anotherVervePattern)) -> newResult = "Verve"
            else -> "Unknown"
        }
        return newResult

    }

    fun isDarkModeEnabled(configuration: Configuration): Boolean {
        return configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun startSMSRetrieverClient(context: Context) {
        val client = SmsRetriever.getClient(context)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener { aVoid -> }
        task.addOnFailureListener { e -> }
    }

    fun validatePhoneNumbers(phoneNumber: String): Boolean {
        val nigeriaPhoneNumber =  """^[789]{1}[01]{1}[0-9]{8}$""".toRegex()
        return phoneNumber.matches(nigeriaPhoneNumber)
    }

     fun getBitMap(view: View): Bitmap {
        val bitMap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitMap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitMap
    }

    //    fun initPartnerId(): String {
//        var partnerID = ""
//        val bankList = mapOf(
//            "12" to "What was the name of your first pet?",
//            "13" to "What is your mother's maiden name?",
//            "14" to "What is the name of your favorite teacher?",
//            "15" to "What was the first concert you attended?",
//            "16" to "What is your favorite book?",
//            "17" to "What is the name of the street you grew up on?",
//            "18" to "What is your favorite movie?",
//            "19" to "What was the make and model of your first car?",
//            "20" to "What is your favorite color?",
//            "21" to "What is the name of the city where you were born?",
//        )
//
//        for (element in bankList) {
//            if (element.key == BuildConfig.FLAVOR) {
//                partnerID = element.value
//            }
//        }
//        return partnerID
//    }
    fun displaySecurityQuestion(): ArrayList<NewGetSecurityQuestionResponseItem> {
        return arrayListOf(
            NewGetSecurityQuestionResponseItem(12, "What was the name of your first pet?"),
            NewGetSecurityQuestionResponseItem(13, "What is your mother's maiden name?"),
            NewGetSecurityQuestionResponseItem(14, "What is the name of your favorite teacher?"),
            NewGetSecurityQuestionResponseItem(15, "What was the first concert you attended?"),
            NewGetSecurityQuestionResponseItem(16, "What is your favorite book?"),
            NewGetSecurityQuestionResponseItem(
                17,
                "What is the name of the street you grew up on?"
            ),
            NewGetSecurityQuestionResponseItem(18, "What is your favorite movie?"),
            NewGetSecurityQuestionResponseItem(
                19,
                "What was the make and model of your first car?"
            ),
            NewGetSecurityQuestionResponseItem(20, "What is your favorite color?"),
            NewGetSecurityQuestionResponseItem(
                21,
                "What is the name of the city where you were born?"
            )
        )
    }

}
