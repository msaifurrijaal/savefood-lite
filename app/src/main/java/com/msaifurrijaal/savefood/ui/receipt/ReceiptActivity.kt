package com.msaifurrijaal.savefood.ui.receipt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.ActivityReceiptBinding
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity.Companion.FOOD_ITEM

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding
    private var food: Food? = null
    private var paymentMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                if (!binding.rbCash.isChecked && !binding.rbDana.isChecked && !binding.rbGopay.isChecked) {
                    binding.rbCash.setError(getString(R.string.choose_an_option))
                    binding.rbSaveFood.setError(getString(R.string.choose_an_option))
                    binding.rbGopay.setError(getString(R.string.choose_an_option))
                    binding.rbDana.setError(getString(R.string.choose_an_option))
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