package edu.nuce.apps.newflowtypes.data

import edu.nuce.apps.newflowtypes.data.model.User
import edu.nuce.apps.newflowtypes.data.model.response.AuthUser
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("users/{id}")
    suspend fun loginUsers(@Path("id") id: Long): AuthUser

    @GET("auth/{id}")
    suspend fun getAuthUserInfo(@Path("id") id: Long): User
}