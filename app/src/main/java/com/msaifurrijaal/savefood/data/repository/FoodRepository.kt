package com.msaifurrijaal.savefood.data.repository

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Food
import java.io.ByteArrayOutputStream
import kotlin.math.log

class FoodRepository(application: Application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val foodDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("foods")
    private val currentUser = firebaseAuth.currentUser

    fun uploadImage(bitmap: Bitmap): LiveData<Resource<String>> {
        val uploadResult = MutableLiveData<Resource<String>>()
        uploadResult.value = Resource.Loading()

        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${currentUser?.uid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        val uploadTask = imageRef.putBytes(imageData)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    uploadResult.value = Resource.Success(uri.toString())
                }
            } else {
                uploadResult.value = Resource.Error("Failed to upload image")
            }
        }
        return uploadResult
    }

    fun createItemFood(
        productName: String,
        description: String,
        categoryFood: String?,
        expirationDate: String,
        price: Double,
        location: String,
        imageUrl: String?,
        latitude: String,
        longitude: String
    ): LiveData<Resource<Boolean>>
    {
        val createItemFoodLiveData = MutableLiveData<Resource<Boolean>>()
        createItemFoodLiveData.value = Resource.Loading()

        val foodId = foodDatabase.push().key

        if (foodId != null) {
            foodDatabase.child(foodId).setValue(
                Food(
                    idFood = foodId,
                    idUploader = currentUser!!.uid.toString(),
                    productName = productName,
                    description = description,
                    category = categoryFood,
                    expirationDate = expirationDate,
                    price = price,
                    location = location,
                    latitude = latitude,
                    longitude = longitude,
                    imageUrl = imageUrl
                )
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        createItemFoodLiveData.value = Resource.Success(true)
                    } else {
                        createItemFoodLiveData.value = Resource.Error("Failed to upload food product")
                    }
                }
                .addOnFailureListener {
                    val message = it.message.toString()
                    createItemFoodLiveData.value = Resource.Error(message)
                }
        }  else {
            createItemFoodLiveData.value = Resource.Error("Failed to upload food product")
        }
        return createItemFoodLiveData
    }

    fun getListFood(): LiveData<Resource<List<Food>>> {
        val foodLiveData = MutableLiveData<Resource<List<Food>>>()
        foodLiveData.value = Resource.Loading()

        foodDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val foodList = mutableListOf<Food>()

                    for (foodSnapshot in dataSnapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)
                        food?.let {
                            foodList.add(it)
                        }
                    }
                    foodLiveData.value = Resource.Success(foodList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    foodLiveData.value = Resource.Error(databaseError.message)
                }
            })
        return foodLiveData
    }

    fun getListFoodByCategory(category: String): LiveData<Resource<List<Food>>> {
        val foodLiveData = MutableLiveData<Resource<List<Food>>>()
        foodLiveData.value = Resource.Loading()

        foodDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val foodList = mutableListOf<Food>()

                for (foodSnapshot in dataSnapshot.children) {
                    val food = foodSnapshot.getValue(Food::class.java)
                    food?.let {
                        if (it.category == category) {
                            foodList.add(it)
                        }
                    }
                }
                foodLiveData.value = Resource.Success(foodList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                foodLiveData.value = Resource.Error(databaseError.message)
            }
        })

        return foodLiveData
    }
}