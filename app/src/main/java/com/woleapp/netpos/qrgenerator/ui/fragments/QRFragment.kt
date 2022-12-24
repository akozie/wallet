package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentQRBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionsBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutEnterPasswordPrefBinding
import com.woleapp.netpos.qrgenerator.databinding.LayoutSetPasswordPrefBinding
import com.woleapp.netpos.qrgenerator.ui.adapter.QrAdapter
import com.woleapp.netpos.qrgenerator.ui.adapter.TransactionAdapter
import com.woleapp.netpos.qrgenerator.ui.db.AppDatabase
import com.woleapp.netpos.qrgenerator.ui.model.QrModel
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel
import com.woleapp.netpos.qrgenerator.ui.utils.PREF_QR_PASSWORD
import com.woleapp.netpos.qrgenerator.ui.utils.RandomUtils.alertDialog
import com.woleapp.netpos.qrgenerator.ui.utils.showToast
import timber.log.Timber
import java.util.ArrayList


class QRFragment : Fragment(), QrAdapter.OnQrClick {

    private lateinit var _binding: FragmentQRBinding
    private val binding get() = _binding
    private lateinit var qrAdapter: QrAdapter
    private lateinit var qrDataList: ArrayList<QrModel>
    private lateinit var inputPasswordDialog: android.app.AlertDialog
    private lateinit var passwordEnterBinding: LayoutEnterPasswordPrefBinding
    private lateinit var passwordSetDialog: android.app.AlertDialog
    private lateinit var passwordSetBinding: LayoutSetPasswordPrefBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        passwordEnterBinding =
//            LayoutEnterPasswordPrefBinding.inflate(LayoutInflater.from(requireContext()), null, false)
//
//        passwordSetBinding = LayoutSetPasswordPrefBinding.inflate(
//            LayoutInflater.from(requireContext()),
//            null,
//            false
//        )
//        passwordSetBinding.save.setOnClickListener {
//            if (passwordSetBinding.reprintPasswordEdittext.text.toString() != passwordSetBinding.confirmReprintPasswordEdittext.text.toString()) {
//                Timber.e("password mismatch")
//                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
//            Prefs.putString(
//                PREF_QR_PASSWORD,
//                passwordSetBinding.reprintPasswordEdittext.text.toString()
//            )
//            passwordSetBinding.confirmReprintPasswordEdittext.setText("")
//            passwordSetBinding.reprintPasswordEdittext.setText("")
//            Toast.makeText(requireContext(), "Password Set", Toast.LENGTH_SHORT).show()
//            passwordSetDialog.cancel()
//        }
//        passwordEnterBinding.proceed.setOnClickListener {
//            if (passwordEnterBinding.passwordEdittext.text.toString() == Prefs.getString(
//                    PREF_QR_PASSWORD,
//                    ""
//                )
//            ) {
//                passwordSetDialog.show()
//            }
//        }
//        passwordSetDialog = alertDialog(requireContext(), R.layout.layout_set_password_pref)
//        inputPasswordDialog = alertDialog(requireContext(), R.layout.layout__enter_password_pref)

        _binding = FragmentQRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateQrData()
        qrSetUp()
       // getQrCodes()

        binding.button.setOnClickListener {
            val action = TransactionsFragmentDirections.actionTransactionsFragmentToGenerateMoreQrFragment()
            findNavController().navigate(action)
        }
    }
    private fun qrSetUp() {
        qrAdapter = QrAdapter(qrDataList, this)
        binding.qrRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.qrRecycler.adapter = qrAdapter
    }

    private fun generateQrData(){
        qrDataList = arrayListOf()
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
        qrDataList.add(QrModel("ProvidusBank Verve", "15th of November 15:32"))
    }

    private fun getQrCodes() {
        showToast("Please wait")
        val livedata = AppDatabase.getDatabaseInstance(requireContext()).qrDao().getQrCodes()
        livedata.observe(viewLifecycleOwner) {
            val result = arrayListOf<String>(it[0].data)
            for (i in 0 until result.size){
                if (result.size < 0){
                    showToast("EMpty")
                }else{
                    showToast("Plenty")
                }
                Timber.d("MYQR--->${result}hhhhhhhhh")
            }
            livedata.removeObservers(viewLifecycleOwner)
        }
    }

    override fun viewTransaction(qrModel: QrModel) {
        val action = TransactionsFragmentDirections.actionTransactionsFragmentToDisplayQrFragment2()
        findNavController().navigate(action)
    }

    override fun onQrClicked(qrModel: QrModel) {
        val action = TransactionsFragmentDirections.actionTransactionsFragmentToQrDetailsFragment2()
        findNavController().navigate(action)
    }


//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource(R.xml.preferences, rootKey)
//        val sharedPreferences =
//            PreferenceManager.getDefaultSharedPreferences(requireContext())
//            preferenceScreen[1].isVisible = false
//
//        preferenceScreen[1].setOnPreferenceClickListener {
//            if (Prefs.contains(PREF_QR_PASSWORD)) {
//                passwordEnterBinding.passwordEdittext.setText("")
//                inputPasswordDialog.show()
//            } else {
//                passwordSetDialog.show()
//            }
//            true
//        }
//        sharedPreferences.registerOnSharedPreferenceChangeListener { _, _ ->
//            // Timber.e(Prefs.contains(key).toString())
//            // Timber.e(Prefs.getString(key, ""))
//        }
//    }

}