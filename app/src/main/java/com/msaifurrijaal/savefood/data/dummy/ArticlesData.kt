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

    private val descs = arrayOf(
        "\t\tDonasi makanan untuk anak-anak jalanan di Malang adalah inisiatif yang bernilai kemanusiaan yang luar biasa. Malang, seperti kota-kota lainnya, dihadapkan dengan permasalahan anak-anak yang tinggal di jalanan, yang seringkali berjuang untuk memenuhi kebutuhan mendasar mereka, terutama dalam hal makanan. Anak-anak ini sering kali terpinggirkan, kurang akses ke pendidikan, dan mungkin telah menghadapi berbagai tantangan dalam kehidupan mereka yang masih sangat muda.\n\n\t\tInisiatif donasi makanan adalah langkah positif yang dilakukan oleh banyak kelompok masyarakat, LSM, individu, dan lembaga amal. Tujuan utama dari program-program ini adalah untuk memastikan bahwa anak-anak jalanan tersebut mendapatkan makanan yang cukup dan bergizi setiap hari. Donatur dapat menyumbangkan makanan segar, paket makanan siap saji, atau bahkan memberikan sumbangan finansial untuk mendukung pembelian makanan.\n\n\t\tDonasi makanan bukan hanya tentang memberikan bantuan fisik, tetapi juga tentang memberikan perasaan kepedulian dan pengakuan kepada anak-anak ini. Ini adalah upaya untuk memastikan bahwa mereka merasa diperhatikan oleh masyarakat, dan mereka memiliki hak untuk hidup sehat dan layak.",
        "\t\tDalam sebuah laporan yang menggemparkan, disebutkan bahwa warga Indonesia, dalam satu tahun, membuang-buang makanan hingga mencapai hampir 200 kilogram. Ini merupakan masalah serius yang perlu mendapatkan perhatian lebih lanjut.\n\n\t\tPembuangan makanan yang mencapai jumlah sedemikian besar tentu saja memiliki konsekuensi yang merugikan. Di satu sisi, ketika sebagian besar orang masih berjuang untuk mendapatkan makanan yang cukup, membuang makanan secara berlebihan menjadi hal yang sangat disayangkan. Bukan hanya masalah moral, namun juga menyumbang terhadap masalah kelaparan di beberapa daerah.\n\n\t\tKetidakefisienan dalam manajemen makanan, seperti pembuangan makanan yang masih layak konsumsi, juga memberikan tekanan tambahan pada masalah lingkungan. Dengan makanan yang dibuang, kita juga membuang sumber daya yang digunakan dalam produksi, transportasi, dan distribusi makanan tersebut. Dampaknya adalah peningkatan limbah dan jejak karbon yang tidak perlu.\n\n\t\tJadi, meminimalkan pembuangan makanan adalah suatu keharusan. Ini adalah panggilan bagi kita semua untuk lebih bijaksana dalam manajemen makanan dan untuk memahami konsekuensi dari setiap potongan makanan yang kita buang. Dengan tindakan yang lebih berkelanjutan, kita dapat membantu mengurangi jumlah makanan yang terbuang dan memberikan dampak positif pada masyarakat dan lingkungan.",
        "\t\tPertanyaan tentang perbandingan nutrisi antara susu nabati dan susu sapi merupakan perdebatan yang telah lama berlangsung di dunia makanan dan gizi. Untuk menjawabnya, perlu dipahami bahwa susu nabati dan susu sapi memiliki komposisi nutrisi yang berbeda.\n\n\t\tSecara umum, susu sapi mengandung protein hewani yang kaya akan asam amino esensial, kalsium, dan vitamin D. Ini menjadikan susu sapi sebagai sumber utama nutrisi seperti protein dan kalsium bagi tubuh manusia. Namun, bagi mereka yang menghindari produk susu hewani karena alasan diet, alergi, atau etika, susu nabati menjadi alternatif yang populer. Susu nabati seperti susu kedelai atau almond juga mengandung nutrisi penting seperti protein nabati, serat, serta vitamin dan mineral.\n\n\t\tMeskipun susu nabati dapat menjadi pilihan yang sehat dan bernutrisi, penting untuk diingat bahwa sumber nutrisi yang diperoleh dari susu nabati biasanya berbeda dalam jumlah dan jenis dibandingkan susu sapi. Oleh karena itu, dalam membandingkan keduanya, faktor keseimbangan dan variasi dalam pola makan menjadi sangat penting. Bagi banyak orang, perbandingan ini menjadi masalah pribadi dan dipengaruhi oleh kebutuhan gizi individual.",
        "\t\tProblematika limbah makanan atau food waste telah menjadi suatu masalah yang semakin memprihatinkan di berbagai belahan dunia, termasuk Indonesia. Malapetaka ini berkaitan erat dengan banyak aspek, mulai dari isu ketahanan pangan hingga dampak lingkungan yang merugikan.\n\n\t\tDi sisi satu, kita sering melihat berita mengenai kelaparan dan kurangnya akses makanan yang berkualitas di banyak tempat di dunia, sementara di sisi lainnya, terdapat pemborosan makanan yang tidak terkendali. Makanan yang dihasilkan dengan susah payah dari proses pertanian, peternakan, dan perikanan seringkali terbuang sia-sia. Tidak hanya itu, proses pembuangan makanan ini juga menjadi beban lingkungan yang serius, mengingat sisa makanan yang membusuk di tempat pembuangan sampah akan menghasilkan gas metana, yang berperan dalam pemanasan global.\n\n\t\tOleh karena itu, penanganan food waste perlu menjadi prioritas dalam upaya mencapai ketahanan pangan global dan menjaga keseimbangan lingkungan. Langkah-langkah inovatif, mulai dari mengurangi pemborosan makanan di tingkat rumah tangga hingga mendukung redistribusi makanan yang masih layak konsumsi, menjadi langkah awal yang krusial dalam mengatasi malapetaka food waste."
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
                article.desc = descs[position]

                list.add(article)
            }
            return list
        }
}