package com.msaifurrijaal.savefood.ui.myproduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.databinding.ActivityMyProductBinding

class MyProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            ibBackMyProduct.setOnClickListener {
                finish()
            }
        }
    }
}