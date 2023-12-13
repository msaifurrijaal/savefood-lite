package com.msaifurrijaal.savefood.ui.additem

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityAddItemBinding
import com.msaifurrijaal.savefood.databinding.LayoutCameraOrGalleryBinding
import com.msaifurrijaal.savefood.ui.location.LocationActivity
import com.msaifurrijaal.savefood.utils.createCustomTempFile
import com.msaifurrijaal.savefood.utils.hideSoftKeyboard
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private var location: String? = null
    private var currentLocation: LatLng? = null
    private var imageProduct: Bitmap? = null
    private var categoryFood: String? = null
    private var intentType = ""
    private var food: Food? = null
    private val addItemViewModel: AddItemViewModel by viewModels()
    private lateinit var dialogLoading: AlertDialog
    private var dataUser: User? = null
    private lateinit var myCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogLoading = showDialogLoading(this)
        myCalendar = Calendar.getInstance()

        getInformationFromIntent()
        setDataInformationFromIntent()
        getDataUser()
        beforeTakePhoto()
        onAction()

    }

    private fun setDataInformationFromIntent() {
        if (intentType.equals(getString(R.string.edit))) {
            food.let {
                binding.apply {
                    etProductName.setText(food?.productName)
                    etDescription.setText(food?.description)
                    etExpirationDate.setText(food?.expirationDate)
                    etPrice.setText(food?.price?.toInt().toString())
                    etLocation.setText(food?.location)
                    if (food?.category.equals("Sell")) {
                        rbSell.isChecked = true
                    } else {
                        rbDonation.isChecked = true
                        etPrice.isEnabled = false
                    }

                    Glide.with(this@AddItemActivity)
                        .load(food?.imageUrl)
                        .into(ivImageUpload)

                    currentLocation = LatLng(
                        food?.latitude!!.toDouble(),
                        food?.longitude!!.toDouble()
                    )

                    categoryFood = food?.category

                    containerUploadImage.visibility = View.GONE
                    containerImageUpload.visibility = View.VISIBLE
                    btnReuploadPhoto.visibility = View.VISIBLE

                    btnPost.setText(R.string.save_changes)

                }
            }
        }
    }

    private fun getInformationFromIntent() {
        food = intent.getParcelableExtra(FOOD_ITEM)
        intentType = intent.getStringExtra(INTENT_TYPE).toString()

        Log.d("AddItemActivity", "food : $food")
        Log.d("AddItemActivity", "intentType : $intentType")
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

                else -> {}
            }
        }
    }

    private fun onAction() {
        binding.apply {
            if (!intentType.equals(getString(R.string.edit))) {
                etPrice.setText("0")
            }
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

            ivCalendar.setOnClickListener {
                showDatePicker()
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
                            if (!intentType.equals(getString(R.string.edit))) {
                                uploadImageToServe(
                                    productName,
                                    description,
                                    categoryFood,
                                    expirationDate,
                                    price.toDouble(),
                                    location,
                                    currentLocation!!.latitude.toString(),
                                    currentLocation!!.longitude.toString()
                                )
                            } else {
                                if (imageProduct != null) {
                                    uploadImageToServe(
                                        productName,
                                        description,
                                        categoryFood,
                                        expirationDate,
                                        price.toDouble(),
                                        location,
                                        currentLocation!!.latitude.toString(),
                                        currentLocation!!.longitude.toString()
                                    )
                                } else {
                                    updatedFood(
                                        productName,
                                        description,
                                        categoryFood,
                                        expirationDate,
                                        price.toDouble(),
                                        location,
                                        food?.imageUrl,
                                        currentLocation!!.latitude.toString(),
                                        currentLocation!!.longitude.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updatedFood(
        productName: String,
        description: String,
        categoryFood: String?,
        expirationDate: String,
        price: Double,
        location: String,
        imageUrl: String?,
        latitude: String,
        longitude: String
    ) {
        addItemViewModel.updatedFood(
            Food(
                idFood = food?.idFood,
                productName = productName,
                description = description,
                category = categoryFood,
                expirationDate = expirationDate,
                price = price,
                location = location,
                imageUrl = imageUrl,
                latitude = latitude,
                longitude = longitude
            )
        ).observe(this) { response ->
            when (response) {
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
                        getString(R.string.congratulations_the_food_information_has_been_updated_successfully),
                    )
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            finish()
                        }, 2000)
                }
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            dateCalendar, // Gunakan DatePickerDialog.OnDateSetListener di sini
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private val dateCalendar = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, month)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLable(myCalendar)
    }

    private fun updateLable(myCalendar: Calendar) {
        val myFormat = "MMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        binding.etExpirationDate.setText(sdf.format(myCalendar.time))
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
                afterTakePhoto()
                binding.ivImageUpload.setImageBitmap(imageBitmap)
                imageProduct = imageBitmap
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun uploadImageToServe(
        productName: String,
        description: String,
        categoryFood: String?,
        expirationDate: String,
        price: Double,
        location: String,
        latitude: String,
        longitude: String
    ) {
        addItemViewModel.uploadImage(imageProduct!!).observe(this) { response ->
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
                    if (!intentType.equals(getString(R.string.edit))) {
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
                    } else {
                        updatedFood(
                            productName,
                            description,
                            categoryFood,
                            expirationDate,
                            price.toDouble(),
                            location,
                            imageUrl,
                            currentLocation!!.latitude.toString(),
                            currentLocation!!.longitude.toString()
                        )
                    }
                }

                else -> {}
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
                        getString(R.string.congratulations_the_food_has_been_successfully_posted),
                    )
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            finish()
                        }, 1500)
                }

                else -> {}
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
            if (!intentType.equals(getString(R.string.edit))) {
                containerUploadImage.visibility = View.VISIBLE
                containerImageUpload.visibility = View.GONE
                btnReuploadPhoto.visibility = View.GONE
            }

        }
    }

    private fun afterTakePhoto() {
        binding.apply {
            containerUploadImage.visibility = View.GONE
            containerImageUpload.visibility = View.VISIBLE
            btnReuploadPhoto.visibility = View.VISIBLE
        }
    }

    private fun radioDataChange() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_sell -> {
                    categoryFood = "Sell"
                    binding.etPrice.isEnabled = true
                    removeErrorNotif()
                }

                R.id.rb_donation -> {
                    categoryFood = "Donation"
                    binding.etPrice.setText("0")
                    binding.etPrice.isEnabled = false
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
                when {
                    productName.isEmpty() -> {
                        etProductName.error =
                            getString(R.string.please_fill_in_the_name_of_the_food)
                        etProductName.requestFocus()
                    }

                    description.isEmpty() -> {
                        etDescription.error =
                            getString(R.string.please_fill_in_the_food_description)
                        etDescription.requestFocus()
                    }

                    expirationDate.isEmpty() -> {
                        etExpirationDate.error =
                            getString(R.string.please_fill_in_the_food_expiration_date)
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
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    (imageUser == null) -> {
                        if (intentType.equals(getString(R.string.edit))) {
                            return true
                        } else {
                            Toast.makeText(
                                this@AddItemActivity,
                                getString(R.string.please_take_product_photos),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
        const val INTENT_TYPE = "intent_type"
        const val FOOD_ITEM = "food_item"
    }

}
