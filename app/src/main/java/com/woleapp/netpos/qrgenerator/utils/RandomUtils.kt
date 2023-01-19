package com.woleapp.netpos.qrgenerator.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.model.Status
import com.woleapp.netpos.qrgenerator.model.TransactionModel

object RandomUtils {

    fun generateTransactionData(){
        val transactionDataList = arrayListOf<TransactionModel>()
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("XYZ Supermarket", "20th of November 15:32", "4000"))
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
                    //loadingDialog.requireActivity().finish()
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
        context: Context,
        layout: Int
    ): AlertDialog{
        val dialogView: View = LayoutInflater.from(context)
            .inflate(layout, null)
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setView(dialogView)

        return dialogBuilder.create()
    }

}