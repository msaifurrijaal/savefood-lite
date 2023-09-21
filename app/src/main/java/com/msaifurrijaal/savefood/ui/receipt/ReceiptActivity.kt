package com.msaifurrijaal.savefood.ui.receipt

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.ActivityReceiptBinding
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity.Companion.FOOD_ITEM
import com.msaifurrijaal.savefood.ui.main.MainActivity
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding
    private var food: Food? = null
    private var paymentMethod: String? = null
    private lateinit var receiptViewModel: ReceiptViewModel
    private lateinit var dialogLoading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiptViewModel = ViewModelProvider(this).get(ReceiptViewModel::class.java)
        dialogLoading = showDialogLoading(this)

        getInformationFromIntent()
        setFoodInformation()
        radioDataChange()
        onAction()

    }

    private fun removeErrorNotif() {
        binding.rbCash.setError(null)
        binding.rbSaveFood.setError(null)
        binding.rbGopay.setError(null)
        binding.rbDana.setError(null)
    }

    private fun onAction() {
        binding.apply {
            ibBackAddProduct.setOnClickListener {
                finish()
            }

            btnOrder.setOnClickListener {
                if (food?.category == "Sell") {
                    if (!binding.rbCash.isChecked && !binding.rbDana.isChecked && !binding.rbGopay.isChecked) {
                        binding.rbCash.setError(getString(R.string.choose_an_option))
                        binding.rbSaveFood.setError(getString(R.string.choose_an_option))
                        binding.rbGopay.setError(getString(R.string.choose_an_option))
                        binding.rbDana.setError(getString(R.string.choose_an_option))
                    } else {
                        food?.let {
                            createTransaction(
                                food?.idFood!!,
                                food?.idUploader!!,
                                food?.sellerName!!,
                                food?.productName!!,
                                food?.category!!,
                                food?.price!!,
                                food?.location!!,
                                food?.imageUrl!!,
                                food?.latitude!!,
                                food?.longitude!!,
                                paymentMethod!!
                            )
                        }
                    }
                    } else {
                        food?.let {
                            createTransaction(
                                food?.idFood!!,
                                food?.idUploader!!,
                                food?.sellerName!!,
                                food?.productName!!,
                                food?.category!!,
                                food?.price!!,
                                food?.location!!,
                                food?.imageUrl!!,
                                food?.latitude!!,
                                food?.longitude!!,
                                "cash"
                            )
                        }
                    }
            }
        }
    }


    private fun createTransaction(
        idFood: String,
        idUploader: String,
        sellerName: String,
        productName: String,
        category: String,
        price: Double,
        location: String,
        imageUrl: String,
        latitude: String,
        longitude: String,
        paymentMethod: String
    ) {
        receiptViewModel.createTransaction(
            idSeller = idUploader,
            sellerName = sellerName,
            productName = productName,
            category = category,
            price = price,
            location = location,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl,
            paymentMethod = paymentMethod
        ).observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@ReceiptActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    deactiveFood(idFood)
                }
            }
        }
    }

    private fun deactiveFood(foodId: String) {
        receiptViewModel.deactiveFood(foodId, getString(R.string.deactive)).observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@ReceiptActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val dialogSuccess =
                        showDialogSuccess(
                            this,
                            getString(R.string.order_successful_please_go_to_the_transaction_page_to_check_the_status),
                        )
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.data = Uri.parse("app://main/history")
                            startActivity(intent)
                            finishAffinity()
                        }, 2000)
                }
            }
        }
    }


    private fun radioDataChange() {
        binding.radioGroup1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_cash -> {
                    paymentMethod = "cash"
                    removeErrorNotif()
                }
                R.id.rb_save_food -> {
                    paymentMethod = "savefood"
                    removeErrorNotif()
                }
                R.id.rb_gopay -> {
                    paymentMethod = "gopay"
                    removeErrorNotif()
                }
                R.id.rb_dana -> {
                    paymentMethod = "dana"
                    removeErrorNotif()
                }
            }
        }
    }

    private fun setFoodInformation() {
        food?.let {
            binding.apply {
                checkDonateOrSell()

                var totalPayment: Double = 0.0

                Glide.with(this@ReceiptActivity)
                    .load(food?.imageUrl)
                    .into(ivProduct)

                tvSellerName.text = food?.sellerName
                tvFoodName.text = food?.productName
                tvFoodPrice.text = "Rp ${food?.price?.toInt()}"
                tvTotalFoodPrice.text = "Rp ${food?.price?.toInt()}"
                if (food?.category == "Sell") {
                    tvShippingCost.text = getString(R.string.rp_1_000)
                    totalPayment = food?.price?.plus(1000) as Double
                } else {
                    tvShippingCost.text = getString(R.string._0)
                }
                tvTotalPayment.text = "Rp ${totalPayment.toInt()}"
            }
        }
    }

    private fun checkDonateOrSell() {
        if (food?.category == "Sell") {
            binding.apply {
                containerPayment.visibility = View.VISIBLE
                viewLinesItemFood.visibility = View.GONE
                tvDonateItem.visibility = View.GONE
                btnOrder.setText(getString(R.string.order))
            }
        } else {
            binding.apply {
                containerPayment.visibility = View.GONE
                btnOrder.setText(getString(R.string.take))
            }
        }
    }

    private fun getInformationFromIntent() {
        food = intent.getParcelableExtra<Food>(FOOD_ITEM)
    }

}