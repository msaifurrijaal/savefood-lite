package com.msaifurrijaal.savefood.ui.forgotpassword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.UserRepository

class ForgotPassViewModel(application: Application) : AndroidViewModel(application) {

    val repository = UserRepository(application)

    fun resetPass(emailUser: String) = repository.resetPassword(emailUser)

}