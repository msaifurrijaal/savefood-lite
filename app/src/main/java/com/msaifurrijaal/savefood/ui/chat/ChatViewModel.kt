package com.msaifurrijaal.savefood.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.ChatRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
): ViewModel() {

    fun getSpesificUser(uidUser: String) = userRepository.getSpesificUser(uidUser)

    fun getAllUsers() = userRepository.getAllUsers()

    fun sendMessage(receiverId: String, message: String) =
        chatRepository.sendChat(receiverId, message)

    fun readChat(receiverId: String) = chatRepository.readChat(receiverId)

}