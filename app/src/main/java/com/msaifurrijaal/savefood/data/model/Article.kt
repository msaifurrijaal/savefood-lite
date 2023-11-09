package com.msaifurrijaal.savefood.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    var title : String = "",
    var photo : Int = 0,
    var desc: String = ""
): Parcelable
