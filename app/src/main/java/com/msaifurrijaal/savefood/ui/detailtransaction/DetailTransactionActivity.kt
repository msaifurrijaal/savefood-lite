package com.msaifurrijaal.savefood.ui.detailtransaction

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Transaction
import com.msaifurrijaal.savefood.databinding.ActivityDetailTransactionBinding
import com.msaifurrijaal.savefood.databinding.LayoutDialogCancelBinding
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransactionBinding
    private lateinit var dialogLoading: AlertDialog
    private var transaction: Transaction? = null
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private val detailTransactionViewModel: DetailTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        dialogLoading = showDialogLoading(this)

        onAction()
        getInformationFromIntent()
        setTransactionInformation()

    }

    private fun onAction() {
        binding.apply {
            ibBackDetailHistory.setOnClickListener {
                finish()
            }

            btnGoesToMaps.setOnClickListener {
                val gmmIntentUri = Uri.parse("google.navigation:q=${transaction!!.latitude},${transaction!!.longitude}")

                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    showDialogError(this@DetailTransactionActivity, getString(R.string.please_install_gmaps_first))
                }
            }

            btnDone.setOnClickListener {
                transaction?.let {
                    detailTransactionViewModel
                        .updateStatus(transaction?.idTransaction!!, "done")
                        .observe(this@DetailTransactionActivity) { response ->
                            when (response) {
                                is Resource.Error -> {
                                    dialogLoading.dismiss()
                                    showDialogError(this@DetailTransactionActivity, response.message.toString())
                                }
                                is Resource.Loading -> {
                                    dialogLoading.show()
                                }
                                is Resource.Success -> {
                                    dialogLoading.dismiss()
                                    if (transaction?.category == "Donation") {
                                        updateUserPoint(transaction?.idSeller)
                                    } else {
                                        val dialogSuccess = showDialogSuccess(
                                            this@DetailTransactionActivity,
                                            getString(R.string.congratulations_the_transaction_has_been_successful),
                                        )
                                        dialogSuccess.show()

                                        Handler(Looper.getMainLooper())
                                            .postDelayed({
                                                dialogSuccess.dismiss()
                                                finish()
                                            }, 2000)
                                    }
                                }
                            }
                        }
                }
            }

            btnCancelOrder.setOnClickListener {
                transaction?.let {
                    val bindingAlert = LayoutDialogCancelBinding.inflate(LayoutInflater.from(this@DetailTransactionActivity))
                    var alertDialog = AlertDialog
                        .Builder(this@DetailTransactionActivity)
                        .setView(bindingAlert.root)
                        .setCancelable(false)
                        .create()

                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    bindingAlert.btnCancel.setOnClickListener {
                        alertDialog.dismiss()
                        detailTransactionViewModel
                            .updateStatus(transaction?.idTransaction!!, "cancel")
                            .observe(this@DetailTransactionActivity) { response ->
                                when (response) {
                                    is Resource.Error -> {
                                        dialogLoading.dismiss()
                                        showDialogError(this@DetailTransactionActivity, response.message.toString())
                                    }
                                    is Resource.Loading -> {
                                        dialogLoading.show()
                                    }
                                    is Resource.Success -> {
                                        dialogLoading.dismiss()
                                        val dialogSuccess = showDialogSuccess(
                                            this@DetailTransactionActivity,
                                            getString(R.string.the_transaction_has_been_successfully_cancelled),
                                        )
                                        dialogSuccess.show()

                                        Handler(Looper.getMainLooper())
                                            .postDelayed({
                                                dialogSuccess.dismiss()
                                                finish()
                                            }, 2000)
                                    }
                                }
                            }
                    }

                    bindingAlert.btnKeep.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    alertDialog.show()
                }
            }

        }

    }

    private fun updateUserPoint(idSeller: String?) {
        detailTransactionViewModel.updateUserPoint(idSeller!!).observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@DetailTransactionActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val dialogSuccess = showDialogSuccess(
                        this@DetailTransactionActivity,
                        getString(R.string.congratulations_the_transaction_has_been_successful),
                        )
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            finish()
                        }, 1500)
                }
            }
        }
    }

    private fun setTransactionInformation() {

        transaction?.let {
            setInformationSelfProduct()

            binding.apply {
                if (transaction?.category == "Donation") {
                    tvPaymentMethod.text = getString(R.string.donation)
                    tvShippingCost.text = "Rp ${getString(R.string._0)}"
                    tvTotalPayment.text = "Rp ${transaction?.price?.toInt().toString()}"
                } else {
                    tvPaymentMethod.text = transaction?.paymentMethod
                    tvShippingCost.text = getString(R.string.rp_1_000)
                    tvTotalPayment.text = "Rp ${transaction?.price?.plus(1000)}"
                }
                tvOrderNumber.text = "Order number : ${transaction?.idTransaction}"
                tvDetailDelivery.text = transaction?.location
                tvFoodName.text = transaction?.productName
                tvFoodPrice.text = "Rp ${transaction?.price?.toInt().toString()}"
                tvSellerName.text = transaction?.sellerName
                tvPaymentTime.text = transaction?.date
            }
        }
    }

    private fun setInformationSelfProduct() {
        binding.apply {
            if (transaction?.idSeller == currentUser?.uid) {
                btnDone.visibility = View.GONE
                btnCancelOrder.visibility = View.VISIBLE
                btnGoesToMaps.visibility = View.GONE
            }
            if (transaction?.idSeller != currentUser?.uid) {
                btnDone.visibility = View.VISIBLE
                btnCancelOrder.visibility = View.VISIBLE
                btnGoesToMaps.visibility = View.VISIBLE
            }
            if (transaction?.status == "done") {
                btnDone.visibility = View.GONE
                btnCancelOrder.visibility = View.GONE
                btnGoesToMaps.visibility = View.GONE
            }
            if (transaction?.status == "cancel") {
                btnDone.visibility = View.GONE
                btnCancelOrder.visibility = View.GONE
                btnGoesToMaps.visibility = View.GONE
            }
        }

    }

    private fun getInformationFromIntent() {
        transaction = intent.getParcelableExtra(TRANSACTION_ITEM)
    }

    companion object {
        const val TRANSACTION_ITEM = "transaction_item"
    }
}