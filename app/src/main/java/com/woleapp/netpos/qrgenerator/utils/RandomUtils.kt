package com.woleapp.netpos.qrgenerator.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.model.Status
import com.woleapp.netpos.qrgenerator.model.wallet.SendWithTallyNumberResponse
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.text.DecimalFormat
import java.util.*

object RandomUtils {

    fun <T> Fragment.observeServerResponse(
        serverResponse: Single<Resource<T>>,
        loadingDialog: AlertDialog,
        compositeDisposable: CompositeDisposable,
        ioScheduler: Scheduler,
        mainThreadSchedulers: Scheduler,
        successAction: () -> Unit
    ) {
        compositeDisposable.add(
            serverResponse.subscribeOn(ioScheduler).observeOn(mainThreadSchedulers)
                .subscribe { data, error ->
                    data?.let {
                        when (it.status) {
                            Status.SUCCESS -> {
                                Log.d("ERROR", it.data.toString())
                                loadingDialog.dismiss()
                                if (
                                    it.data is SendWithTallyNumberResponse
                                ) {
                                    successAction()
                                } else {
                                    showSnackBar(
                                        this.requireView(),
                                        getString(R.string.an_error_occurred)
                                    )
                                }
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
                                showSnackBar(this.requireView(), getString(R.string.timeOut))
                            }
                        }

                    }
                    error?.let {
                        loadingDialog.cancel()
                        loadingDialog.dismiss()
                        showSnackBar(this.requireView(), getString(R.string.an_error_occurred))
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
    }

    fun <T> Fragment.observeServerResponse(
        serverResponse: LiveData<Resource<T>>,
        loadingDialog: ProgressBar,
        fragmentManager: FragmentManager,
        successAction: () -> Unit
    ) {
        serverResponse.observe(this.viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    loadingDialog.visibility = View.GONE
                    successAction()
                }
                Status.LOADING -> {
                    loadingDialog.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    loadingDialog.visibility = View.GONE
                }
                Status.TIMEOUT -> {
                    loadingDialog.visibility = View.GONE
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

    fun getGUID() = UUID.randomUUID().toString().replace("-", "")

    fun stringToBase64(text: String): String {
        val data: ByteArray = text.toByteArray()
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun base64ToPlainText(base64String: String): String {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return String(decodedString)
    }

    fun createClientDataForNonVerveCard(
        transID: String, cardNumber: String, expiryDate: String, cvv: String
    ): String = "$transID:LIVE:$cardNumber:$expiryDate:$cvv::NGN:QR"

    fun createClientDataForVerveCard(
        transID: String, cardNumber: String, expiryDate: String, cvv: String, pin: String
    ): String = "$transID:LIVE:$cardNumber:$expiryDate:$cvv:$pin:NGN:QR"

    fun Number.formatCurrency(currencySymbol: String = "\u20A6"): String {
        val format = DecimalFormat("#,###.00")
        return "$currencySymbol${format.format(this)}"
    }


}