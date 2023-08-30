package com.msaifurrijaal.savefood.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.ChatRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository

class ChatViewModel(application: Application): AndroidViewModel(application) {

    val userRepository = UserRepository(application)
    val chatRepository = ChatRepository(application)

    fun getSpesificUser(uidUser: String) = userRepository.getSpesificUser(uidUser)

    fun getAllUsers() = userRepository.getAllUsers()

    fun sendMessage(receiverId: String, message: String) =
        chatRepository.sendChat(receiverId, message)

    fun readChat(receiverId: String) = chatRepository.readChat(receiverId)

}