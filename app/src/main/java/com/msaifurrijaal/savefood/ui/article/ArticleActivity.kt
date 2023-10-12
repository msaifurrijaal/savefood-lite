package com.msaifurrijaal.savefood.ui.article

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Article
import com.msaifurrijaal.savefood.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private var article: Article? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInformationFromIntent()
        setInformationFromIntent()
        onAction()
    }

    private fun setInformationFromIntent() {
        binding.apply {
            if (article != null) {
                tvTitleArticle.text = article!!.title
                tvWriter.text = article!!.nameWriter
                tvDescArticle.text = article!!.desc

                Glide.with(this@ArticleActivity)
                    .load(article!!.photo)
                    .into(ivArticle)

                Glide.with(this@ArticleActivity)
                    .load(article!!.imgProfileUser)
                    .into(ivUser)
            }
        }
    }

    private fun onAction() {
        binding.apply {
            btnCloseArticlePage.setOnClickListener {
                finish()
            }
        }
    }

    private fun getInformationFromIntent() {
        article = intent.getParcelableExtra(ARTICLE_ITEM)
        Log.d("ArticleActivity", "article : $article")
    }

    companion object {
        const val ARTICLE_ITEM = "article_item"
    }
}