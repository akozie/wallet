package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.adapter.paging.SearchMerchantPagingAdapter
import com.woleapp.netpos.qrgenerator.databinding.FragmentMerchantsBinding
import com.woleapp.netpos.qrgenerator.model.Merchant
import com.woleapp.netpos.qrgenerator.utils.showToast
import com.woleapp.netpos.qrgenerator.viewmodels.MerchantViewModel
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import io.reactivex.disposables.CompositeDisposable


class MerchantsFragment : Fragment(), SearchMerchantPagingAdapter.OnMerchantClick {

    private lateinit var _binding: FragmentMerchantsBinding
    private val binding get() = _binding
    private lateinit var searchedMerchantAdapter: SearchMerchantPagingAdapter
    private val qrViewModel by activityViewModels<QRViewModel>()
    private val merchantViewModel by activityViewModels<MerchantViewModel>()
    private val mDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMerchantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrViewModel.merchantMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable?.isNotEmpty() == true) {
                    searchedMerchantSetUp(editable.toString())
                }
            }
        })
        merchants()
    }

    private fun merchants() {
        searchedMerchantAdapter = SearchMerchantPagingAdapter(this)
        mDisposable.add(merchantViewModel.getAllMerchant().subscribe {
            searchedMerchantAdapter.submitData(lifecycle, it)
        })
        binding.merchantRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.merchantRecyclerView.adapter = searchedMerchantAdapter
        searchedMerchantAdapter.addLoadStateListener { loadState ->
            binding.apply {
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    searchedMerchantAdapter.itemCount < 1
                ) {
                    locatorText.isVisible = true
                    progressIcon.isVisible = false
                } else {
                    locatorText.isVisible = false
                }
            }
        }
    }

    private fun searchedMerchantSetUp(search: String) {
        mDisposable.add(merchantViewModel.getSearchedMerchant(search).subscribe {
            searchedMerchantAdapter.submitData(lifecycle, it)
        })
        binding.merchantRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.merchantRecyclerView.adapter = searchedMerchantAdapter

        searchedMerchantAdapter.addLoadStateListener { loadState ->
            binding.apply {
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    searchedMerchantAdapter.itemCount < 1
                ) {
                    locatorText.isVisible = true
                } else {
                    locatorText.isVisible = false
                }
            }
        }

    }

    override fun onEachMerchantClicked(merchant: Merchant) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToMerchantDetailsFragment(
                merchant
            )
        findNavController().navigate(action)
    }
}