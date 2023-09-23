package com.msaifurrijaal.savefood.ui.myproduct

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
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
import com.msaifurrijaal.savefood.databinding.LayoutDialogCancelBinding
import com.msaifurrijaal.savefood.ui.additem.AddItemActivity
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess

class MyProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProductBinding
    private lateinit var dialogLoading: AlertDialog
    private lateinit var myProductViewModel: MyProductViewModel
    private lateinit var myProductAdapter: MyProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myProductViewModel = ViewModelProvider(this).get(MyProductViewModel::class.java)
        myProductAdapter = MyProductAdapter()
        dialogLoading = showDialogLoading(this)

        observeListMyFood()
        onAction()
        onItemProductClick()
    }

    private fun onItemProductClick() {
        myProductAdapter.btnDeleteClick = { food ->
            food?.let {
                val bindingAlert = LayoutDialogCancelBinding.inflate(LayoutInflater.from(this))
                var alertDialog = AlertDialog
                    .Builder(this)
                    .setView(bindingAlert.root)
                    .setCancelable(false)
                    .create()

                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                bindingAlert.tvMessage.text =
                    getString(R.string.are_you_sure_to_delete_this_product)
                bindingAlert.btnKeep.setText(getString(R.string.cancel))
                bindingAlert.btnCancel.setText(getString(R.string.delete))

                bindingAlert.btnCancel.setOnClickListener {
                    alertDialog.dismiss()
                    myProductViewModel.deleteMyProduct(food.idFood.toString()).observe(this) { response ->
                        when (response) {
                            is Resource.Error -> {
                                dialogLoading.dismiss()
                                showDialogError(this, response.message.toString())
                            }
                            is Resource.Loading -> {
                                dialogLoading.show()
                            }
                            is Resource.Success -> {
                                dialogLoading.dismiss()
                                val dialogSuccess = showDialogSuccess(
                                    this,
                                    getString(R.string.congratulations_your_product_has_been_successfully_deleted),
                                )
                                dialogSuccess.show()

                                Handler(Looper.getMainLooper())
                                    .postDelayed({
                                        dialogSuccess.dismiss()
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

        myProductAdapter.btnEditClick = { food ->
            startActivity(Intent(this, AddItemActivity::class.java)
                .putExtra(AddItemActivity.FOOD_ITEM, food)
                .putExtra(AddItemActivity.INTENT_TYPE, getString(R.string.edit))
            )
        }

        myProductAdapter.onItemClick = { food ->
            startActivity(Intent(this, DetailProductActivity::class.java)
                .putExtra(DetailProductActivity.FOOD_ITEM, food))
        }
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

    private fun actionError() {
        binding.apply {
            pgBarMyProduct.visibility = View.GONE
            rvMyFood.visibility = View.GONE
        }
    }

}