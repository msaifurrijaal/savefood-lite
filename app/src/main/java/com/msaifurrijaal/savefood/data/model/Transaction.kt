package com.msaifurrijaal.savefood.data.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    @get:PropertyName("id_transaction")
    @set:PropertyName("id_transaction")
    var idTransaction: String? = null,
    @get:PropertyName("id_seller")
    @set:PropertyName("id_seller")
    var idSeller: String? = null,
    @get:PropertyName("seller_name")
    @set:PropertyName("seller_name")
    var sellerName: String? = null,
    @get:PropertyName("product_name")
    @set:PropertyName("product_name")
    var productName: String? = null,
    @get:PropertyName("category")
    @set:PropertyName("category")
    var category: String? = null,
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
    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String? = null,
    @get:PropertyName("payment_method")
    @set:PropertyName("payment_method")
    var paymentMethod: String? = null,
    @get:PropertyName("id_buyer")
    @set:PropertyName("id_buyer")
    var idBuyer: String? = null,
    @get:PropertyName("date")
    @set:PropertyName("date")
    var date: String? = null,
): Parcelable