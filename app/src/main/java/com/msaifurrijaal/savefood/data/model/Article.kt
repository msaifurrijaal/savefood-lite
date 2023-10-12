package com.msaifurrijaal.savefood.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    var title : String = "",
    var imgProfileUser : String = "",
    var photo : Int = 0,
    var nameWriter : String = "",
    var desc: String = ""
): Parcelable
