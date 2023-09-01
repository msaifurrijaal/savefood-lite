package com.msaifurrijaal.savefood.ui.additem

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository

class AddItemViewModel(application: Application): AndroidViewModel(application) {

    val foodRepository = FoodRepository(application)
    val userRepository = UserRepository(application)

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
}