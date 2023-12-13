package com.msaifurrijaal.savefood.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.adapter.ListChatAdapter
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.ActivityListChatBinding
import com.msaifurrijaal.savefood.utils.showDialogError
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    @Inject
    lateinit var chatAdapter: ListChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listUserObserve()
        onAction()
    }

    private fun setChatRv(data: List<User>?) {
        data?.let {
            chatAdapter.setListUser(data)
            if (data.size == 0) {
                actionEmpty()
            } else {
                actionNoEmpty()
            }
        }
        binding.rvMyListChat.apply {
            layoutManager = LinearLayoutManager(this@ListChatActivity, LinearLayoutManager.VERTICAL, false)
            adapter = chatAdapter
        }
    }

    private fun onAction() {
        binding.apply {
            ibBackMyListChat.setOnClickListener {
                finish()
            }

            chatAdapter.onItemClick = { userData ->
                startActivity(Intent(this@ListChatActivity, ChatActivity::class.java)
                    .putExtra(ChatActivity.USER_ITEM, userData)
                )
            }
        }
    }

    private fun listUserObserve() {
        chatViewModel.getAllUsers().observe(this) { response ->
            when (response) {
                is Resource.Error -> {
                    actionError()
                    showDialogError(
                        this,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> {
                    actionLoading()
                }
                is Resource.Success -> {
                    setChatRv(response.data)
                }
            }
        }
    }

    private fun actionLoading() {
        binding.apply {
            pgBarMyListChat.visibility = View.VISIBLE
            rvMyListChat.visibility = View.INVISIBLE
        }
    }

    private fun actionEmpty() {
        binding.apply {
            pgBarMyListChat.visibility = View.GONE
            rvMyListChat.visibility = View.GONE
            tvMyListChatEmpty.visibility = View.VISIBLE
        }
    }

    private fun actionNoEmpty() {
        binding.apply {
            pgBarMyListChat.visibility = View.GONE
            rvMyListChat.visibility = View.VISIBLE
            tvMyListChatEmpty.visibility = View.GONE
        }
    }

    private fun actionError() {
        binding.apply {
            pgBarMyListChat.visibility = View.GONE
            rvMyListChat.visibility = View.GONE
        }
    }

}