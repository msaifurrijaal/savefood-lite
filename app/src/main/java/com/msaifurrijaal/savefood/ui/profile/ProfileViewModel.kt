package com.msaifurrijaal.savefood.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.UserRepository

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val repository = UserRepository(application)

    fun getCurrentUser() = repository.getCurrentUser()

    fun deleteUser() = repository.deleteUser()

    fun deleteUserData(uid: String) = repository.deleteUserData(uid)

}