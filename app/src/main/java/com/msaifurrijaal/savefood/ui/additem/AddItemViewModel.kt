package com.msaifurrijaal.savefood.ui.additem

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
): ViewModel() {

    fun getDataUser() = userRepository.getCurrentUser()

    fun uploadImage(bitmap: Bitmap) = foodRepository.uploadImage(bitmap)

    fun createItemFood (
        productName: String,
        description: String,
        categoryFood: String,
        expirationDate: String,
        price: Double,
        location: String,
        imageUrl: String,
        latitude: String,
        longitude: String,
        sellerName: String
    ) = foodRepository.createItemFood(
        productName,
        description,
        categoryFood,
        expirationDate,
        price,
        location,
        imageUrl,
        latitude,
        longitude,
        sellerName
    )

    fun updatedFood(food: Food) = foodRepository.updateFoodData(food)
}