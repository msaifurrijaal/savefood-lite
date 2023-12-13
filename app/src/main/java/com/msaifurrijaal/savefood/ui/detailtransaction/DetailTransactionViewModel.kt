package com.msaifurrijaal.savefood.ui.detailtransaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
): ViewModel() {

    fun updateStatus(idTransaction: String, newStatus: String) = foodRepository.updateStatus(idTransaction, newStatus)

    fun updateUserPoint(uId: String) = userRepository.updateUserPoint(uId)

}