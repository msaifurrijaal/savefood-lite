package com.msaifurrijaal.savefood.ui.listhistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListHistoryViewModel @Inject constructor(
    private val foodRepository: FoodRepository
): ViewModel() {

    fun getListTransaction() = foodRepository.getListTransaction()

    fun getListTransactionFilter(category: String) = foodRepository.getListTransactionFilter(category)

}