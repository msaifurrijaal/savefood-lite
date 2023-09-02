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
import com.msaifurrijaal.savefood.data.model.Transaction
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

class FoodRepository(application: Application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val foodDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("foods")
    private val transactionDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("transactions")
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
        longitude: String,
        sellerName: String
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
                    imageUrl = imageUrl,
                    sellerName = sellerName,
                    status = "active"
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
                            if (it.status == "active") {
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

    fun getListFoodByCategory(category: String): LiveData<Resource<List<Food>>> {
        val foodLiveData = MutableLiveData<Resource<List<Food>>>()
        foodLiveData.value = Resource.Loading()

        foodDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val foodList = mutableListOf<Food>()

                for (foodSnapshot in dataSnapshot.children) {
                    val food = foodSnapshot.getValue(Food::class.java)
                    food?.let {
                        if (it.category == category && it.status == "active") {
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

    fun createItemTransaction(
        idSeller: String,
        sellerName: String,
        productName: String,
        category: String?,
        price: Double,
        location: String,
        imageUrl: String?,
        latitude: String,
        longitude: String,
        paymentMethod: String
    ): LiveData<Resource<Boolean>> {
        val createItemTransactionLiveData = MutableLiveData<Resource<Boolean>>()
        createItemTransactionLiveData.value = Resource.Loading()

        val transactionId = transactionDatabase.push().key
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val formattedDate = dateFormat.format(currentDate)

        if (transactionId != null) {
            transactionDatabase.child(transactionId).setValue(
                Transaction(
                    idTransaction = transactionId,
                    idSeller = idSeller,
                    sellerName = sellerName,
                    productName = productName,
                    category = category,
                    price = price,
                    location = location,
                    latitude = latitude,
                    longitude = longitude,
                    imageUrl = imageUrl,
                    status = "process",
                    paymentMethod = paymentMethod,
                    idBuyer = currentUser?.uid.toString(),
                    date = formattedDate
                )
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        createItemTransactionLiveData.value = Resource.Success(true)
                    } else {
                        createItemTransactionLiveData.value = Resource.Error("Failed to create transaction")
                    }
                }
                .addOnFailureListener {
                    val message = it.message.toString()
                    createItemTransactionLiveData.value = Resource.Error(message)
                }
        } else {
            createItemTransactionLiveData.value = Resource.Error("Failed to create transaction")
        }
        return createItemTransactionLiveData
    }

    fun updateFoodStatus(foodId: String, newStatus: String): LiveData<Resource<Unit>> {
        val updateStatusResult = MutableLiveData<Resource<Unit>>()
        updateStatusResult.value = Resource.Loading()

        val foodRef = foodDatabase.child(foodId)
        foodRef.child("status").setValue(newStatus)
            .addOnSuccessListener {
                updateStatusResult.value = Resource.Success(Unit)
            }
            .addOnFailureListener { error ->
                updateStatusResult.value = Resource.Error(error.message)
            }

        return updateStatusResult
    }

    fun getListTransaction(): LiveData<Resource<List<Transaction>>> {
        val transactionLiveData = MutableLiveData<Resource<List<Transaction>>>()
        transactionLiveData.value = Resource.Loading()

        transactionDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val transactionList = mutableListOf<Transaction>()

                for (transactionSnapshot in dataSnapshot.children) {
                    val transaction = transactionSnapshot.getValue(Transaction::class.java)
                    transaction?.let {
                        if (
                            transaction.idSeller == currentUser?.uid || transaction.idBuyer == currentUser?.uid
                        ) {
                            transactionList.add(it)
                        }
                    }
                }
                transactionLiveData.value = Resource.Success(transactionList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                transactionLiveData.value = Resource.Error(databaseError.message)
            }
        })

        return transactionLiveData
    }

    fun getListTransactionFilter(category: String): LiveData<Resource<List<Transaction>>> {
        val transactionLiveData = MutableLiveData<Resource<List<Transaction>>>()
        transactionLiveData.value = Resource.Loading()

        transactionDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val transactionList = mutableListOf<Transaction>()

                for (transactionSnapshot in dataSnapshot.children) {
                    val transaction = transactionSnapshot.getValue(Transaction::class.java)
                    transaction?.let {
                        if (category == "receiver") {
                            if (it.idBuyer == currentUser?.uid) {
                                transactionList.add(it)
                            }
                        } else {
                            if (it.idSeller == currentUser?.uid) {
                                transactionList.add(it)
                            }
                        }
                    }
                }
                transactionLiveData.value = Resource.Success(transactionList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                transactionLiveData.value = Resource.Error(databaseError.message)
            }
        })

        return transactionLiveData
    }

    fun updateStatus(transactionId: String, newStatus: String): LiveData<Resource<Unit>> {
        val editResult = MutableLiveData<Resource<Unit>>()
        editResult.value = Resource.Loading()

        val userRef = transactionDatabase.child(transactionId)
        userRef.child("status").setValue(newStatus)
            .addOnSuccessListener {
                editResult.value = Resource.Success(Unit)
            }
            .addOnFailureListener { error ->
                editResult.value = Resource.Error(error.message)
            }

        return editResult
    }

}