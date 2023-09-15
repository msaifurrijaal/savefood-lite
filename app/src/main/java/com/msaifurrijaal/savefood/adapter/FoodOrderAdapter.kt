package com.msaifurrijaal.savefood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.LayoutItemFoodOrderBinding

class FoodOrderAdapter: RecyclerView.Adapter<FoodOrderAdapter.ViewHolder>(), Filterable {

    lateinit var onItemClick: ((Food) -> Unit)

    class ViewHolder(var binding: LayoutItemFoodOrderBinding): RecyclerView.ViewHolder(binding.root)

    private var listFood = ArrayList<Food>()
    private var filteredList = ArrayList<Food>() // Daftar yang difilter

    init {
        filteredList = listFood // Inisialisasi daftar yang difilter dengan daftar asli
    }

    fun setFoodList(newListFood: List<Food>) {
        listFood.clear()
        listFood.addAll(newListFood.reversed())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemFoodOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var food = filteredList[position]

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint.isNullOrBlank()) {
                    // Jika input pencarian kosong, kembalikan daftar asli
                    filterResults.values = listFood
                } else {
                    // Lakukan pencarian
                    val query = constraint.toString().trim().lowercase()
                    val filtered = ArrayList<Food>()

                    for (food in listFood) {
                        if (food.productName?.trim()?.lowercase()?.contains(query) == true) {
                            filtered.add(food)
                        }
                    }

                    filterResults.values = filtered
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<Food>
                notifyDataSetChanged()
            }
        }
    }
}