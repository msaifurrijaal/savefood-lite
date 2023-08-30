package com.msaifurrijaal.savefood.ui.listproduct

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.msaifurrijaal.savefood.adapter.FoodOrderAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.FragmentListProductBinding
import com.msaifurrijaal.savefood.ui.detailproduct.DetailProductActivity
import com.msaifurrijaal.savefood.utils.showDialogError

class ListProductFragment() : Fragment() {

    private var _binding: FragmentListProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var listProductViewModel: ListProductViewModel
    private lateinit var foodOrderAdapter: FoodOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListProductBinding.inflate(inflater, container, false)

        listProductViewModel = ViewModelProvider(this).get(ListProductViewModel::class.java)
        foodOrderAdapter = FoodOrderAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerFoodData()
        onItemFoodClick()

    }

    private fun observerFoodData() {
        val slidePosition = arguments?.getInt("slidePosition", 0) ?: 0
        when (slidePosition) {
            0 -> {
                listProductViewModel.getAllFood().observe(viewLifecycleOwner, Observer { response ->
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

            1 -> {
                listProductViewModel.getAllFoodByCcategory("Donation")
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

            2 -> {
                listProductViewModel.getAllFoodByCcategory("Sell")
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
        }
    }

        private fun onItemFoodClick() {
            foodOrderAdapter.onItemClick = { food ->
                startActivity(
                    Intent(activity, DetailProductActivity::class.java)
                        .putExtra(DetailProductActivity.FOOD_ITEM, food))
            }
        }

        private fun setFoodRV(data: List<Food>?) {
            data?.let {
                foodOrderAdapter.setFoodList(data)
            }
            binding.rvFoods.apply {
                layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                adapter = foodOrderAdapter
            }
        }

        private fun actionLoading() {
            binding.apply {
                pgListFood.visibility = View.VISIBLE
                rvFoods.visibility = View.INVISIBLE
            }
        }

        private fun actionSucces() {
            binding.apply {
                pgListFood.visibility = View.GONE
                rvFoods.visibility = View.VISIBLE
            }
        }

        private fun actionError() {
            binding.apply {
                pgListFood.visibility = View.GONE
                rvFoods.visibility = View.GONE
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}



