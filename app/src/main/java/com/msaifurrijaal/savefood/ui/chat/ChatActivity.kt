package com.msaifurrijaal.savefood.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.ChatAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityChatBinding
import com.msaifurrijaal.savefood.utils.showDialogError

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private var chatPartner: User? = null
    private var uidPartner: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        getInformationFromIntent()
        getDataChatPartner(uidPartner)
        setChatRv()
        onAction()
        readChat(uidPartner!!)

    }

    private fun getDataChatPartner(uidUser: String?) {
        chatViewModel.getSpesificUser(uidUser!!).observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    showDialogError(this, response.message.toString())
                    finish()
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    chatPartner = response.data
                    setInformationChat()
                }
            }
        }
    }

    private fun setChatRv() {
        chatAdapter = ChatAdapter()
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }

    private fun onAction() {
        binding.apply {
            btnCloseChat.setOnClickListener {
                finish()
            }

            btnIconSend.setOnClickListener {
                var message = etMessage.text.toString()
                if (message.isEmpty()) {
                    Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                    etMessage.setText("")
                } else {
                    sendMessage(chatPartner!!.uidUser, message)
                    etMessage.setText("")
                }
            }
        }
    }

    private fun sendMessage(uidUser: String?, message: String) {
        chatViewModel.sendMessage(receiverId = uidUser!!, message = message,)
            .observe(this) { response ->
                when(response) {
                    is Resource.Error -> {
                        showDialogError(this, response.message.toString())
                    }
                    is Resource.Loading -> { }
                    is Resource.Success -> {
                        readChat(uidUser)
                    }
                }
            }
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
        uidPartner = intent.getStringExtra(ID_USER)
    }

    companion object {
        const val ID_USER = "id_user"
    }
}