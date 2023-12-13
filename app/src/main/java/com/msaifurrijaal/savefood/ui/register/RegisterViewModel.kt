package com.msaifurrijaal.savefood.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun createAuthUser(email: String, password: String) = userRepository.createAuth(email, password)
    fun createUser(
        uid: String, name:String, email: String, phoneNumber: String, roleUser: String
    ) = userRepository.createUser(uid, name, email, phoneNumber, roleUser)

}