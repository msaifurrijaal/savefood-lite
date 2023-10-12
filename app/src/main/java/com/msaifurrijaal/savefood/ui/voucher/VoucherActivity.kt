package com.msaifurrijaal.savefood.ui.voucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityVoucherBinding

class VoucherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoucherBinding
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformationFromIntent()
        setContentPage()
        onAction()


    }

    private fun setContentPage() {
        if (user != null) {
            binding.apply {
                tvPointUser.text = user!!.userPoint?.toInt().toString()
            }
        }
    }

    private fun getInformationFromIntent() {
        user = intent.getParcelableExtra(USER_ITEM)
    }

    private fun onAction() {
        binding.apply {
            ibBackVoucher.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        const val USER_ITEM = "user"
    }
}