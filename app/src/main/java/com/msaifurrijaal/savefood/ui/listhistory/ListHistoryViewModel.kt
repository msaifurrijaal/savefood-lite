package com.msaifurrijaal.savefood.ui.listhistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository

class ListHistoryViewModel(application: Application): AndroidViewModel(application) {

    private val foodRepository = FoodRepository(application)

    fun getListTransaction() = foodRepository.getListTransaction()

    fun getListTransactionFilter(category: String) = foodRepository.getListTransactionFilter(category)

}