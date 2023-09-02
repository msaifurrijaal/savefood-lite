package com.msaifurrijaal.savefood.ui.detailtransaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository

class DetailTransactionViewModel(application: Application): AndroidViewModel(application) {

    private val foodRepository = FoodRepository(application)

    fun updateStatus(idTransaction: String, newStatus: String) = foodRepository.updateStatus(idTransaction, newStatus)
}