package com.msaifurrijaal.savefood.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getCurrentUser() = userRepository.getCurrentUser()

    fun deleteUser() = userRepository.deleteUser()

    fun deleteUserData(uid: String) = userRepository.deleteUserData(uid)

}