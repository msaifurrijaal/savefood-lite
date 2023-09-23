package com.msaifurrijaal.savefood.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.msaifurrijaal.savefood.databinding.LayoutDialogErrorBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogLoadingBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogSuccessBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun showDialogLoading(context: Context): AlertDialog {
    val binding = LayoutDialogLoadingBinding.inflate(LayoutInflater.from(context))
    var alertDialog = AlertDialog
        .Builder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return alertDialog
}

fun showDialogSuccess(context: Context, message: String): AlertDialog {
    val binding = LayoutDialogSuccessBinding.inflate(LayoutInflater.from(context))
    binding.tvMessage.text = message

    val alertDialog = AlertDialog
        .Builder(context)
        .setView(binding.root)
        .setCancelable(true)
        .create()

    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return alertDialog
}

fun showDialogError(context: Context, message: String){
    val binding = LayoutDialogErrorBinding.inflate(LayoutInflater.from(context))
    binding.tvMessage.text = message

    val alertDialog = AlertDialog
        .Builder(context)
        .setView(binding.root)
        .setCancelable(true)
        .create()

    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()
}

suspend fun LatLng.convertToAddressAsync(context: Context): String? = withContext(
    Dispatchers.IO) {
    val geocode = Geocoder(context, Locale.getDefault())
    val addresses: List<Address>? = try {
        geocode.getFromLocation(this@convertToAddressAsync.latitude, this@convertToAddressAsync.longitude, 1)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    addresses?.takeIf { it.isNotEmpty() }?.get(0)?.getAddressLine(0)
}

fun hideSoftKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}
