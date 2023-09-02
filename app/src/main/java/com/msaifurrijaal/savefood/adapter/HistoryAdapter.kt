package com.msaifurrijaal.savefood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Transaction
import com.msaifurrijaal.savefood.databinding.LayoutItemHistoryReceiverBinding

class HistoryAdapter(private val context: Context): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var firebaseUser: FirebaseUser? = null
    private var transactionList = ArrayList<Transaction>()
    lateinit var onBtnMapsClick: ((Transaction) -> Unit)
    lateinit var onItemClick: ((Transaction) -> Unit)

    fun setHistoryList(newTransactionList: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newTransactionList.reversed())
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: LayoutItemHistoryReceiverBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemHistoryReceiverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var transaction = transactionList[position]
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (transaction.idSeller == firebaseUser?.uid) {
            holder.binding.apply {
                containerHeader.setBackgroundResource(R.drawable.bg_rounded_soft_orange)
                containerTop.setBackgroundColor(context.resources.getColor(R.color.soft_orange))
                tvTitleCard.text = "Sender"
                tvTitleCard.setTextColor(context.resources.getColor(R.color.text_orange))
                btnOpenMaps.visibility = View.GONE
            }
        }

        if (transaction.status == "process") {
            holder.binding.apply {
                btnProcess.setBackgroundResource(R.drawable.bg_btn_stroke_warning)
                btnProcess.setText("Process")
                btnProcess.setTextColor(context.resources.getColor(R.color.text_orange))
            }
        }
        if (transaction.status == "done"){
            holder.binding.btnOpenMaps.visibility = View.GONE
        }
        if (transaction.status == "cancel"){
            holder.binding.apply {
                btnProcess.setBackgroundResource(R.drawable.bg_btn_stroke_danger)
                btnProcess.setText("Cancel")
                btnProcess.setTextColor(context.resources.getColor(R.color.danger_dark))
                btnOpenMaps.visibility = View.GONE
            }

        }

        if (transaction.category == "Donation") {
            holder.binding.apply {
                viewLinesItemFood.visibility = View.GONE
                tvDonateItem.visibility = View.GONE
            }
        }

        holder.binding.apply {

            Glide.with(holder.itemView)
                .load(transaction.imageUrl)
                .into(ivProduct)

            tvFoodSeller.text = transaction.sellerName
            tvFoodName.text = transaction.productName
            tvFoodPrice.text = "Rp ${transaction.price.toInt().toString()}"
            tvDate.text = transaction.date

            btnOpenMaps.setOnClickListener {
                onBtnMapsClick.invoke(transaction)
            }

        }

        holder.itemView.setOnClickListener {
            onItemClick.invoke(transaction)
        }

    }

}