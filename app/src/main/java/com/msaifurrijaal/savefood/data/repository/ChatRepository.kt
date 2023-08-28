package com.msaifurrijaal.savefood.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.Chat

class ChatRepository(application: Application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    fun sendChat(receiverId: String, message: String): LiveData<Resource<Boolean>> {
        val sendChatLiveData = MutableLiveData<Resource<Boolean>>()
        sendChatLiveData.value = Resource.Loading()

        val chatRef = FirebaseDatabase.getInstance().getReference("chats")
        val chatId = chatRef.push().key

        if (chatId != null) {
            chatRef.child(chatId).setValue(
                Chat(
                    uidChat = chatId,
                    senderId = currentUser!!.uid,
                    receiverId = receiverId,
                    message = message
                )
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendChatLiveData.value = Resource.Success(true)
                    } else {
                        sendChatLiveData.value = Resource.Error("Gagal mengirim chat")
                    }
                }
        } else {
            sendChatLiveData.value = Resource.Error("Gagal mengirim chat")
        }
        return sendChatLiveData
    }

    fun readChat(receiverId: String): LiveData<Resource<List<Chat>>> {
        val chatsLiveData = MutableLiveData<Resource<List<Chat>>>()
        chatsLiveData.value = Resource.Loading()

        val chatRef = FirebaseDatabase.getInstance().getReference("chats")

        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatList = mutableListOf<Chat>()

                for (chatSnapshot in dataSnapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    chat?.let {
                        if (chat!!.senderId.equals(currentUser!!.uid) && chat!!.receiverId.equals(receiverId) ||
                            chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(currentUser.uid)
                        ) {
                            chatList.add(it)
                        }                    }
                }

                chatsLiveData.value = Resource.Success(chatList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                chatsLiveData.value = Resource.Error(databaseError.message)
            }
        })

        return chatsLiveData
    }
}