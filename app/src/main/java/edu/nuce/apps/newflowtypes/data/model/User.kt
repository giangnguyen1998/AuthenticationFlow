package edu.nuce.apps.newflowtypes.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val userId: String,
    val fullName: String,
    val email: String?,
    val phoneNumber: String?,
    @SerializedName("createdAt")
    val createTimestamp: String,
    @SerializedName("updatedAt")
    val lastSignInTimestamp: String,
    val isAdmin: Boolean,
    val photoUrl: String
)
