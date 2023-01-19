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
import com.woleapp.netpos.qrgenerator.network.TransactionService
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionService: TransactionService,
) : ViewModel() {

//    fun getBookings(qrCodeID : String) = Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
//        TransactionsPagingSource(transactionService,qrCodeID, 10)
//    }.flow.cachedIn(viewModelScope)

    fun getTransactions(qrCodeID: String): Flowable<PagingData<Transaction>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
            TransactionsPagingSource(transactionService, qrCodeID, 10)
        }.flowable.map { pagingData -> pagingData.filter { true } }
            .cachedIn(viewModelScope)
    }

//    fun getSearchedMerchant(search : String): Flowable<PagingData<Merchant>> {
//        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)){
//            SearchMerchantPagingSource(merchantService,search, 10)
//        }.flowable.map { pagingData -> pagingData.filter { it != null }}
//            .cachedIn(viewModelScope)
//    }

//    override fun getMovies(): Flowable<PagingData<Movies.Movie>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                enablePlaceholders = true,
//                maxSize = 30,
//                prefetchDistance = 5,
//                initialLoadSize = 40),
//            pagingSourceFactory = { pagingSource }
//        ).flowable
//    }
//
//    fun getFavoriteMovies(): Flowable<PagingData<Movies.Movie>> {
//        return repository
//            .getMovies()
//            .map { pagingData -> pagingData.filter { it.poster != null } }
//            .cachedIn(viewModelScope)
//    }


}