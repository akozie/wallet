package com.woleapp.netpos.qrgenerator.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentDisplayQrBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.QrModel
import com.woleapp.netpos.qrgenerator.model.QrModelRequest
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponse
import com.woleapp.netpos.qrgenerator.model.wallet.FetchQrTokenResponseItem
import com.woleapp.netpos.qrgenerator.ui.activities.MainActivity
import com.woleapp.netpos.qrgenerator.utils.QR_FILE_DOWNLOAD
import com.woleapp.netpos.qrgenerator.utils.REQUEST_CODE
import com.woleapp.netpos.qrgenerator.utils.Singletons
import com.woleapp.netpos.qrgenerator.utils.getCurrentDateTimeAsFormattedString
import com.woleapp.netpos.qrgenerator.viewmodels.QRViewModel
import java.io.*


class DisplayQrFragment : Fragment() {

    private lateinit var _binding: FragmentDisplayQrBinding
    private val binding get() = _binding
    private lateinit var viewQr: GenerateQRResponse
    private val qrViewModel by activityViewModels<QRViewModel>()
    private lateinit var getQrModel: QrModelRequest
    private lateinit var outputStream: OutputStream
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDisplayQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getQrModel = Singletons().getSavedQrModelRequest(requireContext())!!
        userName = Singletons().getCurrentlyLoggedInUser(requireContext())?.fullname.toString()
        val qrCode = arguments?.getParcelable<GenerateQRResponse>("DISPLAYQR")?.data
        if (qrCode.isNullOrEmpty()) {
            qrViewModel.generateQrResponse.value?.data?.data?.let {
                Glide.with(requireContext()).load(it).into(binding.qrCode)
            }
        } else {
            Glide.with(requireContext()).load(qrCode).into(binding.qrCode)
        }

            binding.download.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    saveImage();
                } else {
                    askPermission();
                }
            }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please provide the required permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun askPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }

    private fun saveImage() {

        val drawable = binding.qrCode.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val dir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = getQrModel.let {
            File(
                dir,
                "${
                    userName.replace(
                        " ",
                        "_"
                    )
                }_${it.issuing_bank}_${it.card_scheme}_$QR_FILE_DOWNLOAD${getCurrentDateTimeAsFormattedString()}" + ".jpg"
            )
        }

        outputStream = FileOutputStream(file)
        try {
            outputStream = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Toast.makeText(requireContext(), "File successfully Saved", Toast.LENGTH_SHORT).show()
        try {
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}