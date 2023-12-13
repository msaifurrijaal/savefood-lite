package com.msaifurrijaal.savefood.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.data.model.Food
import com.msaifurrijaal.savefood.databinding.LayoutItemMyProductBinding
import javax.inject.Inject

class MyProductAdapter @Inject constructor(): RecyclerView.Adapter<MyProductAdapter.ViewHolder>()  {

    lateinit var onItemClick: ((Food) -> Unit)
    lateinit var btnDeleteClick: ((Food) -> Unit)
    lateinit var btnEditClick: ((Food) -> Unit)

    class ViewHolder(var binding: LayoutItemMyProductBinding): RecyclerView.ViewHolder(binding.root)

    private var listProduct = ArrayList<Food>()
    fun setFoodList(newListFood: List<Food>) {
        listProduct.clear()
        listProduct.addAll(newListFood.reversed())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemMyProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listProduct.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var food = listProduct[position]

        holder.binding.apply {
            tvFoodName.text = food.productName
            tvFoodPrice.text = "Rp ${food.price.toInt().toString()}"
            if (food.category.equals("Sell")) {
                viewLinesItemFood.visibility = View.GONE
                tvDonateItem.visibility = View.GONE
            }

            btnDeleteProduct.setOnClickListener {
                btnDeleteClick.invoke(food)
            }

            btnEditProduct.setOnClickListener {
                btnEditClick.invoke(food)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick.invoke(food)
        }

        Glide.with(holder.itemView)
            .load(food.imageUrl)
            .into(holder.binding.ivMyProduct)

    }
}