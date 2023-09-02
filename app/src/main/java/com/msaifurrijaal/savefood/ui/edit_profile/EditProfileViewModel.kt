package com.msaifurrijaal.savefood.ui.edit_profile

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository

class EditProfileViewModel(application: Application): AndroidViewModel(application) {

    val foodRepository = FoodRepository(application)
    val userRepository = UserRepository(application)

    fun uploadImage(bitmap: Bitmap) = foodRepository.uploadImage(bitmap)

    fun updateUserData(
        uid: String,
        name: String,
        email: String,
        phoneNumber: String,
        avatarUser: String,
        roleUser: String
    ) = userRepository.updateUserData(
        uid,
        name,
        email,
        phoneNumber,
        avatarUser,
        roleUser
    )
}