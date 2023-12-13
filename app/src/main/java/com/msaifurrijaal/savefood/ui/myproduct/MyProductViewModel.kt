package com.msaifurrijaal.savefood.ui.myproduct

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyProductViewModel @Inject constructor(
    private val foodRepository: FoodRepository
): ViewModel() {

    fun getListMyFood() = foodRepository.getListMyFood()

    fun deleteMyProduct(foodId: String) = foodRepository.deleteFood(foodId)

}