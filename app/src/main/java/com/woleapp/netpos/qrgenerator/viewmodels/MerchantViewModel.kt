package com.woleapp.netpos.qrgenerator.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.rxjava2.cachedIn
import androidx.paging.rxjava2.flowable
import com.woleapp.netpos.qrgenerator.model.Merchant
import com.woleapp.netpos.qrgenerator.model.paging.MerchantPagingSource
import com.woleapp.netpos.qrgenerator.model.paging.SearchMerchantPagingSource
import com.woleapp.netpos.qrgenerator.network.MerchantService
import com.woleapp.netpos.qrgenerator.utils.UtilityParam.STRING_MERCHANT_HEADER_TOKEN
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import javax.inject.Inject

@HiltViewModel
class MerchantViewModel @Inject constructor(
    private val merchantService: MerchantService,
) : ViewModel() {

    fun getSearchedMerchant(search: String): Flowable<PagingData<Merchant>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
            SearchMerchantPagingSource(merchantService, search, 20, STRING_MERCHANT_HEADER_TOKEN)
        }.flowable.cachedIn(viewModelScope)
    }

    fun getAllMerchant(): Flowable<PagingData<Merchant>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
            MerchantPagingSource(merchantService,20, STRING_MERCHANT_HEADER_TOKEN)
        }.flowable.cachedIn(viewModelScope)
    }
}