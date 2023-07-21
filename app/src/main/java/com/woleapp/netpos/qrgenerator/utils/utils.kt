package com.woleapp.netpos.qrgenerator.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.woleapp.netpos.qrgenerator.BuildConfig
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.LayoutQrReceiptPdfBinding
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel
import com.woleapp.netpos.qrgenerator.utils.RandomUtils.formatCurrency
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

//convert image to bitmap
fun getBitMap(view: View): Bitmap {
    val bitMap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitMap)
    val bgDrawable = view.background
    if (bgDrawable != null) {
        bgDrawable.draw(canvas)
    } else {
        canvas.drawColor(Color.WHITE)
    }
    view.draw(canvas)
    return bitMap
}

fun initViewsForPdfLayout(
    pdfView: ViewDataBinding,
    receipt: Any?
) {
    when (receipt) {
        is QrTransactionResponseModel -> {
            initViewsForQrReceipt((pdfView as LayoutQrReceiptPdfBinding), receipt)
        }
        else -> { /* Do nothing */
        }
    }
}

private fun initViewsForQrReceipt(
    pdfView: LayoutQrReceiptPdfBinding,
    responseFromWebView: QrTransactionResponseModel?
) {
    pdfView.apply {
        responseFromWebView?.let { respFromWebView: QrTransactionResponseModel ->
            cardOwner.text = pdfView.root.context.getString(
                R.string.card_owner_place_holder,
                respFromWebView.customerName
            )
            dateTime.text =
                pdfView.appVersion.context.getString(
                    R.string.date_time_place_holder,
                    time()
                )
            transAmount.text = pdfView.appVersion.context.getString(
                R.string.amount_place_holder,
                respFromWebView.amount.toDouble().formatCurrency()
            )
            orderId.text = pdfView.appVersion.context.getString(
                R.string.order_id_place_holder,
                respFromWebView.orderId
            )
            narration.text =
                pdfView.appVersion.context.getString(
                    R.string.narration_place_holder,
                    respFromWebView.narration
                        ?: "FAILED"
                )
            transId.text =
                pdfView.appVersion.context.getString(
                    R.string.trans_ref_place_holder,
                    respFromWebView.transId
                )
            status.text =
                pdfView.appVersion.context.getString(
                    R.string.transaction_status_place_holder,
                    respFromWebView.status.uppercase()
                )
            responseCode.text =
                pdfView.appVersion.context.getString(
                    R.string.response_code_place_holder,
                    respFromWebView.code
                )
            message.text = pdfView.appVersion.context.getString(
                R.string.message_place_holder,
                respFromWebView.message
            )
            appVersion.text = pdfView.appVersion.context.getString(
                R.string.app_version_place_holder,
                "${BuildConfig.VERSION_NAME}"
            )
        }
    }
}

fun time(): String {
    val c = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return formatter.format(c)
}

fun createPdf(
    pdfView: ViewDataBinding,
    host: LifecycleOwner
): File {
    val displayMetrics = DisplayMetrics()
    if (host is Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            host.context?.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            host.activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        }
    } else {
        host as Activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            host.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            host.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        }
    }

    pdfView.root.measure(
        View.MeasureSpec.makeMeasureSpec(
            displayMetrics.widthPixels,
            View.MeasureSpec.EXACTLY
        ),
        View.MeasureSpec.makeMeasureSpec(
            displayMetrics.heightPixels,
            View.MeasureSpec.EXACTLY
        )
    )

    pdfView.root.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

    val bitmap = pdfView.root.measuredWidth.let {
        pdfView.root.measuredHeight.let { it1 ->
            Bitmap.createBitmap(
                it,
                it1,
                Bitmap.Config.ARGB_8888
            )
        }
    }

    val canvas = bitmap?.let { Canvas(it) }
    pdfView.root.draw(canvas)

    if (bitmap != null) {
        Bitmap.createScaledBitmap(bitmap, 595, 842, true)
    }
    val pdfDocument = PdfDocument()
    val pageInfo = bitmap?.let {
        PdfDocument.PageInfo.Builder(it.width, it.height, 1).create()
    }
    val page = pdfDocument.startPage(pageInfo)
    if (bitmap != null) {
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
    }
    pdfDocument.finishPage(page)
    val filePath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Tally" + getCurrentDateTimeAsFormattedString() + ".pdf"
    )

    pdfDocument.writeTo(FileOutputStream(filePath))
    pdfDocument.close()

    return filePath
}

fun getCurrentDateTimeAsFormattedString(): String {
    val formattedTime =
        SimpleDateFormat(
            "yyyy-MM-dd hh:mm a",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
            .format(Date())

    return formattedTime.replace(
        formattedTime.takeLast(3),
        "_${formattedTime.takeLast(3).trim()}"
    ).replace(":", "_")
        .replace("-", "_").replace(" ", "_at_")
}


private fun checkForPermission(context: Context, perms: String) =
    EasyPermissions.hasPermissions(
        context,
        perms
    )

fun genericPermissionHandler(
    host: LifecycleOwner,
    context: Context,
    perm: String,
    permCode: Int,
    permRationale: String,
    fn: () -> Unit
) {
    if (checkForPermission(context, perm)) {
        fn()
    } else {
        requestForPermission(
            host,
            permCode,
            permRationale,
            perm
        )
    }
}

private fun requestForPermission(
    host: LifecycleOwner,
    requestCode: Int,
    permissionRationale: String,
    permissionToRequest: String
) {
    if (host is Fragment) {
        EasyPermissions.requestPermissions(
            host,
            permissionRationale,
            requestCode,
            permissionToRequest
        )
    } else {
        host as Activity
        EasyPermissions.requestPermissions(
            host,
            permissionRationale,
            requestCode,
            permissionToRequest
        )
    }
}

fun LifecycleOwner.showSnackBar(rootView: View, message: String) {
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
}
