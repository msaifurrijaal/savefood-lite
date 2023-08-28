package com.msaifurrijaal.savefood.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.ChatAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private var chatPartner: User? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

    }


    private fun readChat(receiverId: String) {
        chatViewModel.readChat(receiverId).observe(this) { response ->
            when(response) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> {
                    chatAdapter.setChatList(response.data!!)
                }
            }
        }
    }

    private fun setInformationChat() {
        chatPartner?.let {
            Glide.with(this)
                .load(chatPartner!!.avatarUser)
                .placeholder(R.color.gray)
                .into(binding.ivUser)

            binding.tvUsername.text = chatPartner!!.nameUser
        }
    }

    private fun getInformationFromIntent() {
        chatPartner = intent.getParcelableExtra<User>(ChatActivity.USER_ITEM)
        Log.d("ChatActivity", chatPartner.toString())
    }

    companion object {
        const val USER_ITEM = "user_item"
    }
}