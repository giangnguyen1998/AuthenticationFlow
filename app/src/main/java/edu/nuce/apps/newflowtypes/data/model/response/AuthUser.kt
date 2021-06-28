package edu.nuce.apps.newflowtypes.data.model.response

import edu.nuce.apps.newflowtypes.data.model.User

data class AuthUser(
    val accessToken: String,
    val refreshToken: String,
    val data: User
)