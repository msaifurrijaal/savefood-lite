package com.msaifurrijaal.savefood.ui.myproduct

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository

class MyProductViewModel(application: Application): AndroidViewModel(application) {

    private val foodRepository = FoodRepository(application)

    fun getListMyFood() = foodRepository.getListMyFood()

    fun deleteMyProduct(foodId: String) = foodRepository.deleteFood(foodId)

}