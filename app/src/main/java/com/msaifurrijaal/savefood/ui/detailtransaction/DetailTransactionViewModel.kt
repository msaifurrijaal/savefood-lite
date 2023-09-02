package com.msaifurrijaal.savefood.ui.detailtransaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository

class DetailTransactionViewModel(application: Application): AndroidViewModel(application) {

    private val foodRepository = FoodRepository(application)
    private val userRepository = UserRepository(application)
    fun updateStatus(idTransaction: String, newStatus: String) = foodRepository.updateStatus(idTransaction, newStatus)

    fun updateUserPoint(uId: String) = userRepository.updateUserPoint(uId)

}