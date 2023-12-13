package com.msaifurrijaal.savefood.ui.edit_profile

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.msaifurrijaal.savefood.data.repository.FoodRepository
import com.msaifurrijaal.savefood.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
): ViewModel() {

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