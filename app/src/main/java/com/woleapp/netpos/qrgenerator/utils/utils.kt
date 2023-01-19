package com.woleapp.netpos.qrgenerator.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

//convert image to bitmap
fun getBitMap(view: View): Bitmap {
    val bitMap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitMap)
    val bgDrawable = view.background
    if (bgDrawable!= null){
        bgDrawable.draw(canvas)
    }else{
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return bitMap
}
