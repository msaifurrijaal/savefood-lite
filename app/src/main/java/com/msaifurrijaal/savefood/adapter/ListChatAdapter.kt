package com.msaifurrijaal.savefood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.LayoutItemChatBinding
import javax.inject.Inject

class ListChatAdapter @Inject constructor(): RecyclerView.Adapter<ListChatAdapter.ViewHolder>() {

    lateinit var onItemClick: ((User) -> Unit)

    private var listUser = ArrayList<User>()

    fun setListUser(newListUser: List<User>) {
        listUser.clear()
        listUser.addAll(newListUser)
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: LayoutItemChatBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user = listUser[position]
        Glide.with(holder.itemView)
            .load(user.avatarUser)
            .into(holder.binding.ivUser)

        holder.binding.tvUsername.text = user.nameUser

        holder.itemView.setOnClickListener {
            onItemClick.invoke(user)
        }

    }
}