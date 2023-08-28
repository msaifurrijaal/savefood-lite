package com.msaifurrijaal.savefood.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)

    fun loginUser(email: String, password: String) = repository.loginUser(email, password)

    fun isAuth() = repository.isAuth()
}