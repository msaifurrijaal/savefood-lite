package com.msaifurrijaal.savefood.ui.detailproduct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.ActivityDetailProductBinding
import com.msaifurrijaal.savefood.ui.chat.ChatActivity
import com.msaifurrijaal.savefood.ui.chat.ChatActivity.Companion.ID_USER

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private var food: Food? = null
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        getInformationFromIntent()
        setFoodInformation()
        onAction()

    }

    private fun onAction() {
        binding.apply {
            btnChat.setOnClickListener {
                startActivity(Intent(this@DetailProductActivity, ChatActivity::class.java)
                    .putExtra(ID_USER, food?.idUploader))
            }

            ivBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun setFoodInformation() {

        food?.let {
            binding.apply {

                setInformationSelfProduct()

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

    private fun setInformationSelfProduct() {
        if (food?.idUploader == currentUser?.uid) {
            binding.apply {
                tvSelfPost.visibility = View.VISIBLE
                btnChat.visibility = View.GONE
                btnOrder.visibility = View.GONE
            }
        } else {
            binding.apply {
                tvSelfPost.visibility = View.GONE
                btnChat.visibility = View.VISIBLE
                btnOrder.visibility = View.VISIBLE
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