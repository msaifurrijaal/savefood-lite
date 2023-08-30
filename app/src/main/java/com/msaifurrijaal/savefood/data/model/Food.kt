package com.msaifurrijaal.savefood.data.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    @get:PropertyName("id_food")
    @set:PropertyName("id_food")
    var idFood: String? = null,
    @get:PropertyName("id_uploader")
    @set:PropertyName("id_uploader")
    var idUploader: String? = null,
    @get:PropertyName("product_name")
    @set:PropertyName("product_name")
    var productName: String? = null,
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,
    @get:PropertyName("category")
    @set:PropertyName("category")
    var category: String? = null,
    @get:PropertyName("expiration_date")
    @set:PropertyName("expiration_date")
    var expirationDate: String? = null,
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Double = 0.0,
    @get:PropertyName("location")
    @set:PropertyName("location")
    var location: String? = null,
    @get:PropertyName("latitude")
    @set:PropertyName("latitude")
    var latitude: String? = null,
    @get:PropertyName("longitude")
    @set:PropertyName("longitude")
    var longitude: String? = null,
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String? = null,
): Parcelable
