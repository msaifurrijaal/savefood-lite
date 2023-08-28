package com.msaifurrijaal.savefood.data.dummy

import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.Article

object ArticlesData {
    private val titles = arrayOf(
        "Donasi Makanan untuk Anak-anak Jalanan di Malang",
        "Kacau! Orang RI Buang-buang Makanan Hampir 200 Kg Setahun",
        "Susu Nabati Rendah Nutrisi Dibandingkan Susu Sapi, Benarkah?",
        "Problematika Food Waste Menjadi Malapetaka"
    )

    private val imgProfileUser = arrayOf(
        "https://randomuser.me/api/portraits/men/5.jpg",
        "https://randomuser.me/api/portraits/women/26.jpg",
        "https://randomuser.me/api/portraits/women/20.jpg",
        "https://randomuser.me/api/portraits/men/16.jpg"
    )

    private val articleImages = intArrayOf(
        R.drawable.article1,
        R.drawable.article2,
        R.drawable.article3,
        R.drawable.article4
    )

    private val username = arrayOf(
        "Dr. Mukri",
        "Salsa Elizabeth",
        "Dr. Antique Aurellie",
        "Tony B."
    )

    val listData : ArrayList<Article>
        get() {
            val list = arrayListOf<Article>()
            for (position in titles.indices) {
                var article = Article()
                article.title = titles[position]
                article.imgProfileUser = imgProfileUser[position]
                article.photo = articleImages[position]
                article.nameWriter = username[position]

                list.add(article)
            }
            return list
        }
}