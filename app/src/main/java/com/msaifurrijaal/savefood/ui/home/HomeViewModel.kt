package com.msaifurrijaal.savefood.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val userRepository = UserRepository(application)
    private val foodRepository = FoodRepository(application)

    fun getCurrentUser() = userRepository.getCurrentUser()

    fun getAllFood() = foodRepository.getListFood()

}