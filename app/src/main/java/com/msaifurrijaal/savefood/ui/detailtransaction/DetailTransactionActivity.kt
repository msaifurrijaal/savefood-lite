package com.msaifurrijaal.savefood.ui.detailtransaction

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Transaction
import com.msaifurrijaal.savefood.databinding.ActivityDetailTransactionBinding
import com.msaifurrijaal.savefood.utils.showDialogError

class DetailTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTransactionBinding
    private var transaction: Transaction? = null
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        onAction()
        getInformationFromIntent()
        setTransactionInformation()

    }

    private fun onAction() {
        binding.apply {
            ibBackDetailHistory.setOnClickListener {
                finish()
            }
        }

        binding.btnGoesToMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${transaction!!.latitude},${transaction!!.longitude}")

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                showDialogError(this, getString(R.string.please_install_gmaps_first))
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
                    tvTotalPayment.text = transaction?.price?.toInt().toString()
                } else {
                    tvPaymentMethod.text = transaction?.paymentMethod
                    tvShippingCost.text = getString(R.string.rp_1_000)
                    tvTotalPayment.text = "Rp ${transaction?.price?.plus(1000)}"
                }
                tvOrderNumber.text = "Order number : ${transaction?.idTransaction}"
                tvDetailDelivery.text = transaction?.location
                tvFoodName.text = transaction?.productName
                tvFoodPrice.text = transaction?.price?.toInt().toString()
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
            if (transaction?.idSeller != currentUser?.uid && transaction?.status == "done") {
                btnDone.visibility = View.GONE
                btnCancelOrder.visibility = View.GONE
                btnGoesToMaps.visibility = View.GONE
            }
            if (transaction?.idSeller == currentUser?.uid && transaction?.status == "done") {
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