package com.msaifurrijaal.savefood.ui.edit_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityEditProfileBinding
import com.msaifurrijaal.savefood.ui.detailtransaction.DetailTransactionActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var userItem: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformationFromIntent()
        setInformationUser()
        onAction()


    }

    private fun setInformationUser() {
        userItem?.let {
            binding.apply {

                Glide.with(this@EditProfileActivity)
                    .load(userItem?.avatarUser)
                    .into(binding.ivUser)

                etFullName.setText(userItem?.nameUser)
                etEmail.setText(userItem?.emailUser)
                etPhoneNumber.setText(userItem?.phoneNumber)
            }
        }
    }

    private fun onAction() {
        binding.apply {
            containerImageUser.setOnClickListener {
                Toast.makeText(this@EditProfileActivity, getString(R.string.sorry_the_edit_user_feature_is_still_not_available), Toast.LENGTH_SHORT).show()
            }

            btnSaveChanges.setOnClickListener {
                Toast.makeText(this@EditProfileActivity, getString(R.string.sorry_the_edit_user_feature_is_still_not_available), Toast.LENGTH_SHORT).show()
            }

            btnBackEditProfile.setOnClickListener {
                finish()
            }
        }
    }

    private fun getInformationFromIntent() {
        userItem = intent.getParcelableExtra(USER_ITEM_PROFILE)
    }


    companion object {
        const val USER_ITEM_PROFILE = "user_item_profile"
    }
}