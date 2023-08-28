package com.msaifurrijaal.savefood.data.model

import com.google.firebase.database.PropertyName

data class Chat(
    @get:PropertyName("sender_id")
    @set:PropertyName("sender_id")
    var senderId: String? = null,
    @get:PropertyName("receiver_id")
    @set:PropertyName("receiver_id")
    var receiverId: String? = null,
    @get:PropertyName("message")
    @set:PropertyName("message")
    var message: String? = null,
    @get:PropertyName("uid_chat")
    @set:PropertyName("uid_chat")
    var uidChat: String? = null
)
