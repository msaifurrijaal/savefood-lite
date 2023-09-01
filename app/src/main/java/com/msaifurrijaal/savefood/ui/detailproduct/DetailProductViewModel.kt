package com.msaifurrijaal.savefood.ui.detailproduct

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.ChatRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository

class DetailProductViewModel(application: Application): AndroidViewModel(application) {

    val userRepository = UserRepository(application)

    fun getSpesificUser(uidUser: String) = userRepository.getSpesificUser(uidUser)
}