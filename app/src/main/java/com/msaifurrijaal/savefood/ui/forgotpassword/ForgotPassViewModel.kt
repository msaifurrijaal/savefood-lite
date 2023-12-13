package com.msaifurrijaal.savefood.ui.forgotpassword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPassViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun resetPass(emailUser: String) = userRepository.resetPassword(emailUser)

}