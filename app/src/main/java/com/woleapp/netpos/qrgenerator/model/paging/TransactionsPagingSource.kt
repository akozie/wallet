package com.woleapp.netpos.qrgenerator.model.paging


import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.woleapp.netpos.qrgenerator.model.Transaction
import com.woleapp.netpos.qrgenerator.model.TransactionResponse
import com.woleapp.netpos.qrgenerator.network.TransactionService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TransactionsPagingSource(
    private val transactionService: TransactionService,
    private val qrCodeID: String,
    private val pageSize: Int
) : RxPagingSource<Int, Transaction>() {
    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        return null
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Transaction>> {
        val position = params.key ?: 1

        return transactionService.getEachTransaction(qrCodeID, position, pageSize)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, position) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun toLoadResult(
        data: TransactionResponse,
        position: Int
    ): LoadResult<Int, Transaction> {
        return LoadResult.Page(
            data = data.data.rows,
            prevKey = if (position == 1) null else position - 1,
            nextKey = if (data.data.rows.isEmpty()) null else position + 1
        )
    }
}