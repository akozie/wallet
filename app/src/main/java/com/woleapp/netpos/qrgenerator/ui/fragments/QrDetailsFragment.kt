package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.woleapp.netpos.qrgenerator.databinding.FragmentQrDetailsBinding
import com.woleapp.netpos.qrgenerator.ui.adapter.QrDetailsAdapter
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel


class QrDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentQrDetailsBinding
    private val binding get() = _binding
    private lateinit var qrViewPager2: ImageView
    private lateinit var qrIndicator: TabLayout

    //    private lateinit var qrDetailsAdapter: QrDetailsViewPagerAdapter
    private lateinit var qrDetailAdapter: QrDetailsAdapter
    private lateinit var qrDetailRecyclerview: RecyclerView
    private lateinit var qrDetailsDataList: ArrayList<TransactionModel>
    private var current = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQrDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrViewPager2 = binding.qrPager
        qrDetailRecyclerview = binding.recyclerview
        // qrIndicator = binding.qrTabLayout
        generateQrDataData()
        //qrDetailsSetUp()
        //setUpQrViewPager()
        qrSetUp()
    }

    private fun qrSetUp() {
        qrDetailAdapter = QrDetailsAdapter(qrDetailsDataList)
        qrDetailRecyclerview.adapter = qrDetailAdapter
        qrDetailRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

//    private fun setUpQrViewPager() {
//        qrViewPager2 = binding.qrPager
//        qrIndicator = binding.qrTabLayout
//    }

//    private fun qrDetailsSetUp() {
//
//        /**
//         * Setup ViewpagerAdapter
//         * */
//        qrDetailsAdapter = QrDetailsViewPagerAdapter()
//        qrDetailsAdapter.submitList(qrDetailsDataList)
//        qrViewPager2.apply {
//            adapter = qrDetailsAdapter
//            clipToPadding = false
//            clipChildren = false
//            offscreenPageLimit = 3
//            setPadding(150, 0, 150, 0)
//        }
//        val marginPageTransformer = MarginPageTransformer(30)
//        val compositionPageTransformer = CompositePageTransformer()
//        compositionPageTransformer.addTransformer(marginPageTransformer)
//        compositionPageTransformer.addTransformer { page, position ->
//            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
//        }
//        //qrViewPager2.setPageTransformer(compositionPageTransformer)
//
////        TabLayoutMediator(qrIndicator, qrViewPager2) { tab, position ->
////            qrViewPager2.setCurrentItem(tab.position, true)
////        }.attach()
//
////        qrDetailsAdapter = QrDetailsViewPagerAdapter(requireContext(),qrDetailsDataList)
////        binding.qrPager.adapter = qrDetailsAdapter
////        binding.qrPager.clipToPadding = false
////        binding.qrPager.clipChildren = false
////        binding.qrPager.offscreenPageLimit = 3
////        binding.qrPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
////
////        val comPosPage = CompositePageTransformer()
////        comPosPage.addTransformer(MarginPageTransformer(40))
////        comPosPage.addTransformer { page,position ->
////            val r:Float = 1 - abs(position)
////            page.scaleY = 0.85f + r * 0.15f
////        }
////
////        binding.qrPager.setPageTransformer(comPosPage)
//    }

    private fun generateQrDataData() {
        qrDetailsDataList = arrayListOf()
        qrDetailsDataList.add(TransactionModel("Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))
        qrDetailsDataList.add(TransactionModel("Prince Supermarket", "24th of November", "5000"))

    }
//    private fun generateQrDataData(){
//        qrDetailsDataList = arrayListOf()
//        qrDetailsDataList.add(QrDetailsModel(1,R.drawable.qr_code, "ProvidusBank Verve","15th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "16th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(2,R.drawable.qr_code, "ProvidusBank Verve","16th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "17th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(3,R.drawable.qr_code, "ProvidusBank Verve","20th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(4,R.drawable.qr_code, "ProvidusBank Verve","20th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(5,R.drawable.qr_code, "ProvidusBank Verve","20th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(6,R.drawable.qr_code, "ProvidusBank Verve","20th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(7,R.drawable.qr_code, "ProvidusBank Verve","20th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "5000")))
//        ))
//        qrDetailsDataList.add(QrDetailsModel(8,R.drawable.qr_code, "ProvidusBank Verve","20th of November 15:32",
//            Transactions(arrayListOf(TransactionModel("Prince Supermarket",  "25th of November", "5000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000"),
//                TransactionModel("Prince Supermarket",  "24th of November", "9000")))
//        ))
//       }
}