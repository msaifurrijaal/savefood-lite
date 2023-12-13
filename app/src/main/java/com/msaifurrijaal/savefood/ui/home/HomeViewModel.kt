package com.msaifurrijaal.savefood.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val foodRepository: FoodRepository
): ViewModel() {

    fun getCurrentUser() = userRepository.getCurrentUser()

    fun getAllFood() = foodRepository.getListFood()

}