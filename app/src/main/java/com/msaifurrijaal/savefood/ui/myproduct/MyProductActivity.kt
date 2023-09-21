package com.msaifurrijaal.savefood.ui.myproduct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.MyProductAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.ActivityMyProductBinding
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading

class MyProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProductBinding
    private lateinit var myProductViewModel: MyProductViewModel
    private lateinit var myProductAdapter: MyProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myProductViewModel = ViewModelProvider(this).get(MyProductViewModel::class.java)
        myProductAdapter = MyProductAdapter()

        observeListMyFood()
        onAction()
    }

    private fun observeListMyFood() {
        myProductViewModel.getListMyFood().observe(this) { response ->
            when(response) {
                is Resource.Error -> {
                    actionError()
                    showDialogError(
                        this,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> {
                    actionLoading()
                }
                is Resource.Success -> {
                    setFoodRv(response.data)
                }
            }

        }
    }

    private fun setFoodRv(data: List<Food>?) {
        data?.let {
            myProductAdapter.setFoodList(data)
            if (data.size == 0) {
                actionEmpty()
            } else {
                actionNoEmpty()
            }
        }
        binding.rvMyFood.apply {
            layoutManager = LinearLayoutManager(this@MyProductActivity, LinearLayoutManager.VERTICAL, false)
            adapter = myProductAdapter
        }
    }

    private fun onAction() {
        binding.apply {
            ibBackMyProduct.setOnClickListener {
                finish()
            }
        }
    }

    private fun actionLoading() {
        binding.apply {
            pgBarMyProduct.visibility = View.VISIBLE
            rvMyFood.visibility = View.INVISIBLE
        }
    }

    private fun actionEmpty() {
        binding.apply {
            pgBarMyProduct.visibility = View.GONE
            rvMyFood.visibility = View.GONE
            tvMyProductEmpty.visibility = View.VISIBLE
        }
    }

    private fun actionNoEmpty() {
        binding.apply {
            pgBarMyProduct.visibility = View.GONE
            rvMyFood.visibility = View.VISIBLE
            tvMyProductEmpty.visibility = View.GONE
        }
    }

    private fun actionSucces() {
        binding.apply {
            pgBarMyProduct.visibility = View.GONE
            rvMyFood.visibility = View.VISIBLE
        }
    }

    private fun actionError() {
        binding.apply {
            pgBarMyProduct.visibility = View.GONE
            rvMyFood.visibility = View.GONE
        }
    }

}