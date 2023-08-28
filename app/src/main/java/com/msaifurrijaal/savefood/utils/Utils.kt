package com.msaifurrijaal.savefood.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.LatLng
import com.msaifurrijaal.savefood.databinding.LayoutDialogErrorBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogLoadingBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogSuccessBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

fun showDialogLoading(context: Context): AlertDialog {
    val binding = LayoutDialogLoadingBinding.inflate(LayoutInflater.from(context))
    return AlertDialog
        .Builder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()
}

fun showDialogSuccess(context: Context): AlertDialog {
    val binding = LayoutDialogSuccessBinding.inflate(LayoutInflater.from(context))

    return AlertDialog
        .Builder(context)
        .setView(binding.root)
        .setCancelable(true)
        .create()
}

fun showDialogError(context: Context, message: String){
    val binding = LayoutDialogErrorBinding.inflate(LayoutInflater.from(context))
    binding.tvMessage.text = message

    AlertDialog
        .Builder(context)
        .setView(binding.root)
        .setCancelable(true)
        .create()
        .show()
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