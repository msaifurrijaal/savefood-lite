package com.msaifurrijaal.savefood.ui.food

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
): ViewModel() {

    fun getAllFood() = foodRepository.getListFood()

    fun getAllFoodByCcategory(category: String) = foodRepository.getListFoodByCategory(category)
}