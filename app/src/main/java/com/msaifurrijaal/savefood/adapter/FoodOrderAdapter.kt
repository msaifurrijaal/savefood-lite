package com.msaifurrijaal.savefood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.LayoutItemFoodOrderBinding

class FoodOrderAdapter: RecyclerView.Adapter<FoodOrderAdapter.ViewHolder>() {

    lateinit var onItemClick: ((Food) -> Unit)

    class ViewHolder(var binding: LayoutItemFoodOrderBinding): RecyclerView.ViewHolder(binding.root)

    private var listFood = ArrayList<Food>()

    fun setFoodList(newListFood: List<Food>) {
        listFood.clear()
        listFood.addAll(newListFood)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemFoodOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listFood.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var food = listFood[position]

        if (food.category.equals("Sell")) {
            holder.binding.viewLinesItemFood.visibility = View.GONE
            holder.binding.tvDonateItem.visibility = View.GONE
        }

        Glide.with(holder.itemView)
            .load(food.imageUrl)
            .into(holder.binding.ivFoodPicture)

        holder.binding.tvFoodName.text = food.productName
        holder.binding.tvFoodPrice.text = "Rp ${food.price.toInt().toString()}"
        holder.binding.tvLocationFood.text = food.location


        holder.itemView.setOnClickListener {
            onItemClick.invoke(food)
        }

    }
}