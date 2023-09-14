package com.msaifurrijaal.savefood.ui.food

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository

class FoodViewModel(application: Application): AndroidViewModel(application) {
    private val foodRepository = FoodRepository(application)

    fun getAllFood() = foodRepository.getListFood()

    fun getAllFoodByCcategory(category: String) = foodRepository.getListFoodByCategory(category)
}