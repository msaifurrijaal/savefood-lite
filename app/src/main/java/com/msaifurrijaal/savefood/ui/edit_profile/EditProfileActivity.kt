package com.msaifurrijaal.savefood.ui.edit_profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityEditProfileBinding
import com.msaifurrijaal.savefood.databinding.LayoutCameraOrGalleryBinding
import com.msaifurrijaal.savefood.ui.additem.AddItemActivity
import com.msaifurrijaal.savefood.ui.detailtransaction.DetailTransactionActivity
import com.msaifurrijaal.savefood.utils.createCustomTempFile
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var userItem: User? = null
    private var roleUser: String? = null
    private var imageUser: Bitmap? = null
    private var imageUrl: String? = null
    private lateinit var dialogLoading: AlertDialog
    private lateinit var editProfileViewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        dialogLoading = showDialogLoading(this)

        getInformationFromIntent()
        setInformationUser()
        onAction()


    }

    private fun onAction() {
        radioDataChange()
        binding.apply {
            containerImageUser.setOnClickListener {
                showChooseDialog()
            }

            btnSaveChanges.setOnClickListener {
                val fullName = etFullName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val numberPhone = etPhoneNumber.text.toString().trim()
                if (!binding.rbCustomer.isChecked && !binding.rbFnb.isChecked && !binding.rbNgo.isChecked) {
                    binding.rbCustomer.setError(getString(R.string.choose_an_option))
                    binding.rbFnb.setError(getString(R.string.choose_an_option))
                    binding.rbNgo.setError(getString(R.string.choose_an_option))
                } else {
                    if (checkValidation(fullName, email, numberPhone)) {
                        if (imageUser != null) {
                            uploadImagesToServe(
                                userItem?.uidUser,
                                fullName,
                                email,
                                numberPhone,
                                roleUser
                            )
                        } else {
                            imageUrl = userItem?.avatarUser
                            editProfileUser(
                                userItem?.uidUser,
                                fullName,
                                email,
                                numberPhone,
                                imageUrl,
                                roleUser
                            )
                        }
                    }
                }

            }

            btnBackEditProfile.setOnClickListener {
                finish()
            }
        }
    }

    private fun editProfileUser(uidUser: String?, fullName: String, email: String, numberPhone: String, imageUrl: String?, roleUser: String?) {
        editProfileViewModel.updateUserData(
            uidUser!!,
            fullName,
            email,
            numberPhone,
            imageUrl!!,
            roleUser!!
        ).observe(this) { response ->
            when (response){
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@EditProfileActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val dialogSuccess = showDialogSuccess(
                        this@EditProfileActivity,
                        getString(R.string.congratulations_the_profile_has_been_successfully_changed)
                    )
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            finish()
                        }, 1500)
                }
            }
        }
    }


    private fun uploadImagesToServe(uidUser: String?, fullName: String, email: String, numberPhone: String, roleUser: String?
    ) {
        editProfileViewModel.uploadImage(imageUser!!).observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@EditProfileActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    imageUrl = response.data
                    editProfileUser(
                        userItem?.uidUser,
                        fullName,
                        email,
                        numberPhone,
                        imageUrl,
                        roleUser
                    )
                }
            }
        }
    }

    fun showChooseDialog() {
        val dialogView = LayoutCameraOrGalleryBinding.inflate(LayoutInflater.from(this))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView.root)

        val alertDialog = alertDialogBuilder.create()

        dialogView.btnCamera.setOnClickListener {
            if (!allPermissionsGranted()) {
                requestCameraPermission()
            } else if (allPermissionsGranted()) {
                startTakePhoto()
            }
            alertDialog.dismiss()
        }

        dialogView.btnGallery.setOnClickListener {
            openGallery()
            alertDialog.dismiss()
        }

        dialogView.btnCancelOrder.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            selectedImg?.let {
                val imageBitmap: Bitmap = getBitmapFromUri(selectedImg)
                binding.ivUser.setImageBitmap(imageBitmap)
                imageUser = imageBitmap
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS_CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS_CAMERA) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (allPermissionsGranted()) {
                startTakePhoto()
            }
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            imageUser =  BitmapFactory.decodeFile(myFile.path)
            binding.ivUser.setImageBitmap(imageUser)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@EditProfileActivity,
                "com.msaifurrijaal.savefood",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
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

    private fun removeErrorNotif() {
        binding.rbNgo.setError(null)
        binding.rbFnb.setError(null)
        binding.rbCustomer.setError(null)
    }

    private fun radioDataChange() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_customer -> {
                    roleUser = "Customer"
                    removeErrorNotif()
                }
                R.id.rb_fnb -> {
                    roleUser = "FNB"
                    removeErrorNotif()
                }
                R.id.rb_ngo -> {
                    roleUser = "NGO"
                    removeErrorNotif()
                }
            }
        }
    }

    private fun checkValidation(fullName: String, email: String, numberPhone: String): Boolean {
        binding.apply {
            when {
                fullName.isEmpty() -> {
                    etFullName.error = getString(R.string.please_fill_in_your_name)
                    etFullName.requestFocus()
                }
                email.isEmpty() -> {
                    etEmail.error = getString(R.string.please_fill_in_your_email)
                    etEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = getString(R.string.please_use_a_valid_email)
                    etEmail.requestFocus()
                }
                numberPhone.isEmpty() -> {
                    etPhoneNumber.error = getString(R.string.please_fill_in_your_phone_number)
                    etPhoneNumber.requestFocus()
                }
                else -> return true
            }
        }
        return false
    }

    private fun getInformationFromIntent() {
        userItem = intent.getParcelableExtra(USER_ITEM_PROFILE)
    }


    companion object {
        const val USER_ITEM_PROFILE = "user_item_profile"
        const val REQUEST_CODE_INTENT = 101
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS_CAMERA = 10
    }
}