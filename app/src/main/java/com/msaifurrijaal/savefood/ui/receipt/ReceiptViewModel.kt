package com.msaifurrijaal.savefood.ui.receipt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository

class ReceiptViewModel(application: Application): AndroidViewModel(application) {

    private val foodRepository = FoodRepository(application)

    fun createTransaction(
        idSeller: String,
        sellerName: String,
        productName: String,
        category: String?,
        price: Double,
        location: String,
        imageUrl: String?,
        latitude: String,
        longitude: String,
        paymentMethod: String
    )
    = foodRepository.createItemTransaction(
        idSeller = idSeller,
        sellerName = sellerName,
        productName = productName,
        category = category,
        price = price,
        location = location,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        paymentMethod = paymentMethod
    )

    fun deactiveFood(foodId: String, newStatus: String) = foodRepository.updateFoodStatus(foodId, newStatus)
}