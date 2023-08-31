package com.msaifurrijaal.savefood.ui.additem

import android.Manifest
import android.app.Activity
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
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityAddItemBinding
import com.msaifurrijaal.savefood.databinding.LayoutCameraOrGalleryBinding
import com.msaifurrijaal.savefood.ui.location.LocationActivity
import com.msaifurrijaal.savefood.utils.createCustomTempFile
import com.msaifurrijaal.savefood.utils.hideSoftKeyboard
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess
import java.io.File

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private var location: String? = null
    private var currentLocation: LatLng? = null
    private var imageProduct: Bitmap? = null
    private var categoryFood: String? = null
    private lateinit var addItemViewModel: AddItemViewModel
    private lateinit var dialogLoading: AlertDialog
    private var dataUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addItemViewModel = ViewModelProvider(this).get(AddItemViewModel::class.java)
        dialogLoading = showDialogLoading(this)

        getDataUser()
        beforeTakePhoto()
        onAction()

    }

    private fun removeErrorNotif() {
        binding.rbSell.setError(null)
        binding.rbDonation.setError(null)
    }

    private fun getDataUser() {
        addItemViewModel.getDataUser().observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    showDialogError(this, getString(R.string.failed_to_get_user_data_please_try_again))
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    dataUser = response.data
                }
            }
        }
    }

    private fun onAction() {
        binding.apply {
            etPrice.setText("0")
            radioDataChange()
            ivLocation.setOnClickListener {
                val intent = Intent(this@AddItemActivity, LocationActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_INTENT)
            }

            ivUploadImage.setOnClickListener {
                showChooseDialog(this@AddItemActivity)
            }

            btnReuploadPhoto.setOnClickListener {
                showChooseDialog(this@AddItemActivity)
            }

            ibBackAddProduct.setOnClickListener {
                finish()
            }

            btnPost.setOnClickListener {
                val productName = etProductName.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val expirationDate = etExpirationDate.text.toString().trim()
                val price = etPrice.text.toString().trim()
                val location = etLocation.text.toString().trim()

                if (!binding.rbSell.isChecked && !binding.rbDonation.isChecked) {
                    binding.rbSell.setError(getString(R.string.choose_an_option))
                    binding.rbDonation.setError(getString(R.string.choose_an_option))
                } else {
                    if (checkValidation(productName, description, categoryFood, expirationDate, price, location, imageProduct)) {
                        hideSoftKeyboard(this@AddItemActivity, binding.root)
                        if (dataUser != null) {
                            uploadImageToServe(
                                productName,
                                description,
                                categoryFood,
                                expirationDate,
                                price.toDouble(),
                                location,
                                imageProduct,
                                currentLocation!!.latitude.toString(),
                                currentLocation!!.longitude.toString()
                            )
                        }
                    }
                }
            }
        }
    }

    fun showChooseDialog(context: Context) {
        val dialogView = LayoutCameraOrGalleryBinding.inflate(LayoutInflater.from(context))

        val alertDialogBuilder = AlertDialog.Builder(context)
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
            Toast.makeText(context, "Open Gallery", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        dialogView.btnCancelOrder.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun uploadImageToServe(
        productName: String,
        description: String,
        categoryFood: String?,
        expirationDate: String,
        price: Double,
        location: String,
        imageUser: Bitmap?,
        latitude: String,
        longitude: String
    ) {
        addItemViewModel.uploadImage(imageUser!!).observe(this) { response ->
            when(response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@AddItemActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val imageUrl = response.data
                    createItemFood(
                        productName,
                        description,
                        categoryFood as String,
                        expirationDate,
                        price,
                        location,
                        imageUrl,
                        latitude,
                        longitude
                    )
                }
            }
        }
    }

    private fun createItemFood(
        productName: String,
        description: String,
        categoryFood: String,
        expirationDate: String,
        price: Double,
        location: String,
        imageUrl: String?,
        latitude: String,
        longitude: String
    ) {
        addItemViewModel.createItemFood(
            productName = productName,
            description = description,
            categoryFood = categoryFood,
            expirationDate = expirationDate,
            price = price,
            location = location,
            imageUrl = imageUrl as String,
            latitude = latitude,
            longitude = longitude,
            dataUser!!.nameUser!!
        ).observe(this) { response ->
            when(response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(this@AddItemActivity, response.message.toString())
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val dialogSuccess = showDialogSuccess(
                        this,
                        getString(R.string.congratulations_the_food_has_been_successfully_posted)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                location = data?.getStringExtra("updatedLocation")
                currentLocation = LatLng(
                    data?.getStringExtra("Lat")!!.toDouble(),
                    data?.getStringExtra("Lng")!!.toDouble()
                )
                binding.etLocation.setText(location)
            }
        }
    }

    // camera
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

            imageProduct =  BitmapFactory.decodeFile(myFile.path)
            afterTakePhoto()
            binding.ivImageUpload.setImageBitmap(imageProduct)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddItemActivity,
                "com.msaifurrijaal.savefood",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun beforeTakePhoto() {
        binding.apply {
            containerUploadImage.visibility = View.VISIBLE
            ivUploadImage.visibility = View.VISIBLE
            containerImageUpload.visibility = View.GONE
            cvImageUpload.visibility = View.GONE
            ivImageUpload.visibility = View.GONE
            btnReuploadPhoto.visibility = View.GONE
        }
    }

    private fun afterTakePhoto() {
        binding.apply {
            containerUploadImage.visibility = View.GONE
            ivUploadImage.visibility = View.GONE
            containerImageUpload.visibility = View.VISIBLE
            cvImageUpload.visibility = View.VISIBLE
            ivImageUpload.visibility = View.VISIBLE
            btnReuploadPhoto.visibility = View.VISIBLE
        }
    }

    private fun radioDataChange() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_sell -> {
                    categoryFood = "Sell"
                    removeErrorNotif()
                }
                R.id.rb_donation -> {
                    categoryFood = "Donation"
                    removeErrorNotif()
                }
            }
        }
    }

    private fun checkValidation(
        productName: String,
        description: String,
        categoryFood: String?,
        expirationDate: String,
        price: String,
        location: String,
        imageUser: Bitmap?
    ): Boolean {
        binding.apply {
            when{
                productName.isEmpty() -> {
                    etProductName.error = getString(R.string.please_fill_in_the_name_of_the_food)
                    etProductName.requestFocus()
                }
                description.isEmpty() -> {
                    etDescription.error = getString(R.string.please_fill_in_the_food_description)
                    etDescription.requestFocus()
                }
                expirationDate.isEmpty() -> {
                    etExpirationDate.error = getString(R.string.please_fill_in_the_food_expiration_date)
                    etExpirationDate.requestFocus()
                }
                price.isEmpty() -> {
                    etPrice.error = getString(R.string.please_fill_in_the_price_of_the_food)
                    etPrice.requestFocus()
                }
                location.isEmpty() -> {
                    Toast.makeText(
                        this@AddItemActivity,
                        getString(R.string.please_press_the_location_logo_on_the_left),
                        Toast.LENGTH_SHORT).
                    show()
                }
                (imageUser == null) -> {
                    Toast.makeText(this@AddItemActivity, getString(R.string.please_take_product_photos), Toast.LENGTH_SHORT).show()
                }
                else -> return true
            }
        }
        return false
    }

    companion object {
        const val REQUEST_CODE_INTENT = 101
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS_CAMERA = 10
    }

}