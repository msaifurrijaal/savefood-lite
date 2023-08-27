package com.msaifurrijaal.savefood.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.msaifurrijaal.savefood.databinding.LayoutDialogErrorBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogLoadingBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogSuccessBinding

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