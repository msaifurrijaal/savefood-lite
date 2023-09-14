package com.msaifurrijaal.savefood.ui.food

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.ArticleAdapter
import com.msaifurrijaal.savefood.adapter.FoodHomeAdapter
import com.msaifurrijaal.savefood.adapter.FoodOrderAdapter
import com.msaifurrijaal.savefood.adapter.TabsFoodAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.FragmentFoodBinding
import com.msaifurrijaal.savefood.ui.home.HomeViewModel
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

