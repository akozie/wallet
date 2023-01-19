package com.woleapp.netpos.qrgenerator.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.woleapp.netpos.qrgenerator.databinding.FragmentShowQrBinding
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.utils.REQUEST_CODE
import com.woleapp.netpos.qrgenerator.utils.getBitMap
import java.io.*


class ShowQrFragment : Fragment() {


    private lateinit var _binding: FragmentShowQrBinding
    private val binding get() = _binding
    private lateinit var outputStream : OutputStream

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShowQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrCode = arguments?.getParcelable<GenerateQRResponse>("QR")?.data
        if (qrCode.isNullOrEmpty()) {
            Glide.with(requireContext()).load(com.woleapp.netpos.qrgenerator.R.drawable.providus_verve).into(binding.qrCode)
        } else {
            Glide.with(requireContext()).load(qrCode).into(binding.qrCode)
        }

        binding.share.setOnClickListener {
            share()
        }

        binding.download.setOnClickListener {
            binding.download.setOnClickListener {
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    saveImage();
                }else {
                    askPermission();

                }
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

    private fun share() {
        val b: Bitmap = getBitMap(binding.qrCode)

        try {
            val f = File(requireContext().externalCacheDir, "forShare.jpg")
            val outputStream = FileOutputStream(f)
            b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            f.setReadable(true, false)


            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            val photo: Uri = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName + ".provider",
                f
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, photo)
            shareIntent.type = "image/*"
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, null))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun saveImage() {

        val drawable = binding.qrCode.getDrawable() as BitmapDrawable
        val bitmap = drawable.bitmap
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
        // val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, System.currentTimeMillis().toString() + ".jpg")

        outputStream = FileOutputStream(file)
        try {
            outputStream = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Toast.makeText(requireContext(), "Successfuly Saved", Toast.LENGTH_SHORT).show()
        try {
            outputStream!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            outputStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}