package com.woleapp.netpos.qrgenerator.model.paging



import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.woleapp.netpos.qrgenerator.model.*
import com.woleapp.netpos.qrgenerator.network.MerchantService
import com.woleapp.netpos.qrgenerator.network.QRService
import com.woleapp.netpos.qrgenerator.network.TransactionService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

class SearchMerchantPagingSource(
    private val merchantService: MerchantService,
    private val search:String,
    private val limit:Int,
) : RxPagingSource<Int, Merchant>() {
    override fun getRefreshKey(state: PagingState<Int, Merchant>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Merchant>> {
        val position = params.key ?: 1
        return merchantService.getMerchant(search, limit, position)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, position) }
            .onErrorReturn { LoadResult.Error(it) }
    }

    private fun toLoadResult(data: MerchantResponse, position: Int): LoadResult<Int, Merchant> {
        return LoadResult.Page(
            data = data.data.rows,
            prevKey = if (position == 1) null else position-1,
            nextKey =  if (data.data.rows.isEmpty()) null else position+1,
        )
    }

}