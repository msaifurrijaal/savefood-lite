package com.msaifurrijaal.savefood.data.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @get:PropertyName("uid_user")
    @set:PropertyName("uid_user")
    var uidUser: String? = null,
    @get:PropertyName("name_user")
    @set:PropertyName("name_user")
    var nameUser: String? = null,
    @get:PropertyName("email_user")
    @set:PropertyName("email_user")
    var emailUser: String? = null,
    @get:PropertyName("phone_number")
    @set:PropertyName("phone_number")
    var phoneNumber: String? = null,
    @get:PropertyName("role_user")
    @set:PropertyName("role_user")
    var roleUser: String? = null,
    @get:PropertyName("verified")
    @set:PropertyName("verified")
    var verified: Boolean = false,
    @get:PropertyName("user_point")
    @set:PropertyName("user_point")
    var userPoint: Double? = null,
    @get:PropertyName("avatar_user")
    @set:PropertyName("avatar_user")
    var avatarUser: String? = null
) : Parcelable
