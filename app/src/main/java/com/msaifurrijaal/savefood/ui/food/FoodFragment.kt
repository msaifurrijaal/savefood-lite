package com.msaifurrijaal.savefood.ui.food

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.FoodOrderAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.FragmentFoodBinding
import com.msaifurrijaal.savefood.ui.chat.ListChatActivity
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity
import com.msaifurrijaal.savefood.utils.hideSoftKeyboard
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading

class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: AlertDialog
    private lateinit var foodOrderAdapter: FoodOrderAdapter
    private lateinit var foodViewModel: FoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodBinding.inflate(inflater, container, false)

        dialogLoading = showDialogLoading(requireContext())
        foodOrderAdapter = FoodOrderAdapter()
        foodViewModel = ViewModelProvider(this).get(FoodViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rbAll.isChecked = true;
        observerFoodData()
        onItemFoodClick()
        onAction()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onAction() {
        binding.apply {
            etSearchMain.addTextChangedListener {
                foodOrderAdapter.filter.filter(it.toString())
            }

            ivChat.setOnClickListener {
                startActivity(Intent(requireContext(), ListChatActivity::class.java))
            }

            root.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val imm = binding.root.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    return@setOnTouchListener true
                }
                return@setOnTouchListener false
            }

            etSearchMain.setOnEditorActionListener {_, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val dataSearch = etSearchMain.text.toString().trim()
                    foodOrderAdapter.filter.filter(dataSearch)
                    hideSoftKeyboard(requireContext(), binding.root)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener true
            }
        }
    }

    private fun observerFoodData() {
        getAllData()
        binding.rbFilterProduct.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_all -> {
                    getAllData()
                }

                R.id.rb_sell -> {
                    getDataByCategory("Sell")
                }

                R.id.rb_donation -> {
                    getDataByCategory("Donation")
                }
            }
        }
    }

    private fun onItemFoodClick() {
        foodOrderAdapter.onItemClick = { food ->
            startActivity(
                Intent(activity, DetailProductActivity::class.java)
                    .putExtra(DetailProductActivity.FOOD_ITEM, food))
        }
    }

    private fun getAllData() {
        foodViewModel.getAllFood().observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Error -> {
                    actionError()
                    showDialogError(
                        requireContext(),
                        response.message.toString()
                    )
                }

                is Resource.Loading -> {
                    actionLoading()
                }

                is Resource.Success -> {
                    actionSucces()
                    setFoodRV(response.data)
                }
            }
        })
    }

    private fun getDataByCategory(category: String) {
        foodViewModel.getAllFoodByCcategory(category)
            .observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Error -> {
                        actionError()
                        showDialogError(
                            requireContext(),
                            response.message.toString()
                        )
                    }

                    is Resource.Loading -> {
                        actionLoading()
                    }

                    is Resource.Success -> {
                        actionSucces()
                        setFoodRV(response.data)
                    }
                }
            })
    }

    private fun setFoodRV(data: List<Food>?) {
        data?.let {
            foodOrderAdapter.setFoodList(data)
            if (data.isEmpty()) {
                actionEmpty()
            } else {
                actionNoEmpty()
            }
        }

        binding.rvFoods.apply {
            layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            adapter = foodOrderAdapter
        }
    }

    private fun actionNoEmpty() {
        binding.apply {
            pgListFood.visibility = View.GONE
            rvFoods.visibility = View.VISIBLE
            tvProductEmpty.visibility = View.GONE
        }
    }

    private fun actionEmpty(){
        binding.apply {
            pgListFood.visibility = View.GONE
            rvFoods.visibility = View.VISIBLE
            tvProductEmpty.visibility = View.VISIBLE
        }
    }


    private fun actionError() {
        binding.apply {
            pgListFood.visibility = View.GONE
            rvFoods.visibility = View.GONE
        }
    }

    private fun actionSucces() {
        binding.apply {
            pgListFood.visibility = View.GONE
            rvFoods.visibility = View.VISIBLE
        }
    }

    private fun actionLoading() {
        binding.apply {
            pgListFood.visibility = View.VISIBLE
            rvFoods.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

