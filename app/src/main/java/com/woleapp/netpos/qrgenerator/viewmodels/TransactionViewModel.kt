package com.woleapp.netpos.qrgenerator.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.flowable
import com.woleapp.netpos.qrgenerator.model.Transaction
import com.woleapp.netpos.qrgenerator.model.paging.TransactionsPagingSource
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.network.TransactionRepository
import com.woleapp.netpos.qrgenerator.network.TransactionService
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionService: TransactionService,
    private val transactionRepository: TransactionRepository,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    fun getTransactions(qrCodeID: String): Flowable<PagingData<Transaction>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
            TransactionsPagingSource(transactionService, qrCodeID, 10)
        }.flowable.map { pagingData -> pagingData.filter { true } }
            .cachedIn(viewModelScope)
    }

    fun saveQrTransaction(qrTransactionResponseModel: QrTransactionResponseModel) {
        compositeDisposable.add(
            transactionRepository.saveQrTransaction(qrTransactionResponseModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        //  Log.d("DATA", it.toString())
                    }
                    error?.let {
                        //   Log.d("ERROR", it.localizedMessage)
                    }
                }
        )
    }

}