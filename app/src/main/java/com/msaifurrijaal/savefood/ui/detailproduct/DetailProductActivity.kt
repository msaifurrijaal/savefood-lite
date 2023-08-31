package com.msaifurrijaal.savefood.ui.detailproduct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityDetailProductBinding
import com.msaifurrijaal.savefood.ui.chat.ChatActivity
import com.msaifurrijaal.savefood.ui.chat.ChatActivity.Companion.USER_ITEM
import com.msaifurrijaal.savefood.ui.receipt.ReceiptActivity
import com.msaifurrijaal.savefood.utils.showDialogError

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private var food: Food? = null
    private lateinit var detailProductViewModel: DetailProductViewModel
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var seller: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        detailProductViewModel = ViewModelProvider(this).get(DetailProductViewModel::class.java)

        getInformationFromIntent()
        getDataSeller(food?.idUploader)
        onAction()

    }

    private fun getDataSeller(uidUser: String?) {
        detailProductViewModel.getSpesificUser(uidUser!!).observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    showDialogError(this, response.message.toString())
                    finish()
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    seller = response.data
                    setFoodInformation()
                }
            }
        }
    }

    private fun onAction() {
        binding.apply {
            btnChat.setOnClickListener {
                seller?.let {
                    startActivity(Intent(this@DetailProductActivity, ChatActivity::class.java)
                        .putExtra(USER_ITEM, seller))
                }
            }

            ivBack.setOnClickListener {
                finish()
            }

            btnOrder.setOnClickListener {
                food?.let {
                    startActivity(Intent(this@DetailProductActivity, ReceiptActivity::class.java)
                        .putExtra(FOOD_ITEM, food))
                }
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

                Glide.with(this@DetailProductActivity)
                    .load(seller?.avatarUser)
                    .into(ivSeller)

                tvFoodName.text = food?.productName
                tvSellerName.text = seller?.nameUser
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