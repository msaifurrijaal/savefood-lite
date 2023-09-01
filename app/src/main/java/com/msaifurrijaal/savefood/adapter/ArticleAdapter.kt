package com.msaifurrijaal.savefood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.data.model.Article
import com.msaifurrijaal.savefood.databinding.LayoutItemArticleBinding

class ArticleAdapter: RecyclerView.Adapter<ArticleAdapter.ViewHolder>()  {

    private var articleList = ArrayList<Article>()

    fun setArticleList(newArticleList: List<Article>) {
        articleList.clear()
        articleList.addAll(newArticleList)
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: LayoutItemArticleBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var artikel = articleList[position]

        Glide.with(holder.itemView)
            .load(artikel.photo)
            .into(holder.binding.ivArtikel)

        Glide.with(holder.itemView)
            .load(artikel.imgProfileUser)
            .into(holder.binding.ivWriter)

        holder.binding.tvTitle.text = artikel.title
        holder.binding.tvNameWriter.text = artikel.nameWriter

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Sorry, the article feature is still not available", Toast.LENGTH_SHORT).show()
        }
    }
}