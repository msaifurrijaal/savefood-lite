package com.msaifurrijaal.savefood.ui.detailproduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.ActivityDetailProductBinding

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private var food: Food? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformationFromIntent()
        setFoodInformation()

    }

    private fun setFoodInformation() {

        food?.let {
            binding.apply {

                if (food?.category == "Sell") {
                    binding.apply {
                        viewLinesItemFood.visibility = View.GONE
                        tvDonateItem.visibility = View.GONE
                        btnOrder.setText(getString(R.string.order))
                    }
                }

                Glide.with(this@DetailProductActivity)
                    .load(food?.imageUrl)
                    .into(ivFood)

                tvFoodName.text = food?.productName
                tvFoodDesc.text = food?.description
                tvFoodPrice.text = "Rp ${food?.price?.toInt().toString()}"
                tvFoodLocation.text = food?.location
                tvFoodDateExpired.text = food?.expirationDate

            }
        }
    }

    private fun getInformationFromIntent() {
        food = intent.getParcelableExtra<Food>(FOOD_ITEM)
    }

    companion object {
        const val FOOD_ITEM = "food_tem"
    }

}