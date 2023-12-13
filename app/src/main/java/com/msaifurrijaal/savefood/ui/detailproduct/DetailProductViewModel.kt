package com.msaifurrijaal.savefood.ui.detailproduct

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.ChatRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getSpesificUser(uidUser: String) = userRepository.getSpesificUser(uidUser)
}