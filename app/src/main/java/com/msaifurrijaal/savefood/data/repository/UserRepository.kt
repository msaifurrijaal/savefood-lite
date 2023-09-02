package com.msaifurrijaal.savefood.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User

class UserRepository(application: Application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    private val currentUser = firebaseAuth.currentUser

    fun createAuth(email: String, password: String): LiveData<Resource<FirebaseUser>> {
        val authResult = MutableLiveData<Resource<FirebaseUser>>()

        authResult.value = Resource.Loading()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                val user = it.result.user
                authResult.value = Resource.Success(user)
            }
            .addOnFailureListener {
                val message = it.message.toString()
                authResult.value = Resource.Error(message)
            }
        return authResult
    }

    fun createUser(
        uid:String, name: String, email: String, phoneNumber: String, roleUser: String
    ): LiveData<Resource<Boolean>> {
        val userResult = MutableLiveData<Resource<Boolean>>()
        userResult.postValue(Resource.Loading())
        userDatabase.child(uid.toString())
            .setValue(
                User(
                    uidUser = uid,
                    nameUser = name,
                    emailUser = email,
                    phoneNumber = phoneNumber,
                    roleUser = roleUser,
                    verified = false,
                    userPoint = 0.0,
                    avatarUser = "https://ui-avatars.com/api/?background=218B5E&color=fff&size=100&rounded=true&name=$name"
                )
            )
            .addOnSuccessListener {
                userResult.postValue(Resource.Success(true))
            }
            .addOnFailureListener {
                val message = it.message
                userResult.postValue(Resource.Error(message))
            }
        return userResult
    }

    fun loginUser(email: String, password: String): LiveData<Resource<FirebaseUser>> {
        val authResult = MutableLiveData<Resource<FirebaseUser>>()

        authResult.value = Resource.Loading()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = it.user
                authResult.value = Resource.Success(user)
            }
            .addOnFailureListener {
                val message = it.message.toString()
                authResult.value = Resource.Error(message)
            }
        return authResult
    }

    fun isAuth(): LiveData<Resource<FirebaseUser>> {
        val authResult = MutableLiveData<Resource<FirebaseUser>>()

        val currentUser = firebaseAuth.currentUser
        authResult.value = Resource.Error("")
        currentUser?.let {
            authResult.value = Resource.Success(it)
        }
        return authResult
    }

    fun resetPassword(emailUser: String): LiveData<Resource<String>> {
        val resetPasswordLiveData = MutableLiveData<Resource<String>>()
        resetPasswordLiveData.value = Resource.Loading()

        firebaseAuth.sendPasswordResetEmail(emailUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    resetPasswordLiveData.value = Resource.Success("Email reset password berhasil dikirim")
                } else {
                    resetPasswordLiveData.value = Resource.Error("Gagal mengirim email reset password")
                }
            }
        return resetPasswordLiveData
    }

    fun getCurrentUser(): LiveData<Resource<User>> {
        val currentUserLiveData = MutableLiveData<Resource<User>>()
        currentUserLiveData.value = Resource.Loading()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userReference = userDatabase.child(uid)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        currentUserLiveData.value = Resource.Success(user)
                    } else {
                        currentUserLiveData.value = Resource.Error("User data not found")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    currentUserLiveData.value = Resource.Error(databaseError.message)
                }
            })
        } else {
            currentUserLiveData.value = Resource.Error("User not authenticated")
        }
        return currentUserLiveData
    }

    fun getSpesificUser(uidUser: String): LiveData<Resource<User>> {
        val currentUserLiveData = MutableLiveData<Resource<User>>()
        currentUserLiveData.value = Resource.Loading()

        val userReference = userDatabase.child(uidUser)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    currentUserLiveData.value = Resource.Success(user)
                } else {
                    currentUserLiveData.value = Resource.Error("User data not found")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                currentUserLiveData.value = Resource.Error(databaseError.message)
            }
        })
        return currentUserLiveData
    }

    fun getAllUsers(): LiveData<Resource<List<User>>> {
        val usersLiveData = MutableLiveData<Resource<List<User>>>()
        usersLiveData.value = Resource.Loading()

        userDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()

                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        if (!user.uidUser.equals(currentUser!!.uid)) {
                            userList.add(it)
                        }
                    }
                }

                usersLiveData.value = Resource.Success(userList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                usersLiveData.value = Resource.Error(databaseError.message)
            }
        })

        return usersLiveData
    }

    fun updateUserPoint(uid: String): LiveData<Resource<Boolean>> {
        val userResult = MutableLiveData<Resource<Boolean>>()
        userResult.postValue(Resource.Loading())

        userDatabase.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        val currentPoints = it.userPoint
                        val updatedPoints = currentPoints?.plus(100.0)

                        userDatabase.child(uid).child("user_point").setValue(updatedPoints)
                            .addOnSuccessListener {
                                userResult.postValue(Resource.Success(true))
                            }
                            .addOnFailureListener { error ->
                                val message = error.message
                                userResult.postValue(Resource.Error(message))
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val message = databaseError.message
                userResult.postValue(Resource.Error(message))
            }
        })

        return userResult
    }

}