package edu.nuce.apps.newflowtypes.shared.data.signin

import android.net.Uri

/**
 * Interface to decouple the user info from network (firebase)
 */
interface AuthenticatedUserInfo : AuthenticatedUserInfoBasic, AuthenticatedUserInfoRegistered

/**
 * Basic user info
 */
interface AuthenticatedUserInfoBasic {

    fun isSignedIn(): Boolean

    fun getEmail(): String?

    fun getPhoneNumber(): String?

    fun getLastSignInTimestamp(): Long?

    fun getCreationTimestamp(): Long?

    fun isAdmin(): Boolean?

    fun getUid(): String?

    fun getDisplayName(): String?

    fun getPhotoUrl(): Uri?
}

/**
 * Extra information about the auth and registration state of the user
 */
interface AuthenticatedUserInfoRegistered {

    fun isRegistered(): Boolean

    fun isRegistrationDataReady(): Boolean
}