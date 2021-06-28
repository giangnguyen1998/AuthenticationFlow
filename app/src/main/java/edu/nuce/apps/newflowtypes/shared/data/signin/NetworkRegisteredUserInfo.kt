package edu.nuce.apps.newflowtypes.shared.data.signin

import android.net.Uri
import androidx.core.net.toUri
import edu.nuce.apps.newflowtypes.data.model.User
import edu.nuce.apps.newflowtypes.shared.util.toLongDate

class NetworkRegisteredUserInfo(
    private val basicUserInfo: AuthenticatedUserInfoBasic?,
    private val isRegistered: Boolean?
) : AuthenticatedUserInfo {

    override fun isSignedIn(): Boolean = basicUserInfo?.isSignedIn() == true

    override fun getEmail(): String? = basicUserInfo?.getEmail()

    override fun getPhoneNumber(): String? = basicUserInfo?.getPhoneNumber()

    override fun getLastSignInTimestamp(): Long? = basicUserInfo?.getLastSignInTimestamp()

    override fun getCreationTimestamp(): Long? = basicUserInfo?.getCreationTimestamp()

    override fun isAdmin(): Boolean? = basicUserInfo?.isAdmin()

    override fun getUid(): String? = basicUserInfo?.getUid()

    override fun getDisplayName(): String? = basicUserInfo?.getDisplayName()

    override fun getPhotoUrl(): Uri? = basicUserInfo?.getPhotoUrl()

    override fun isRegistered(): Boolean = isRegistered ?: false

    override fun isRegistrationDataReady(): Boolean = isRegistered != null
}

class NetworkUserInfo(
    private val networkUser: User?
) : AuthenticatedUserInfoBasic {

    override fun isSignedIn(): Boolean = networkUser != null

    override fun getEmail(): String? = networkUser?.email

    override fun getPhoneNumber(): String? = networkUser?.phoneNumber

    override fun getLastSignInTimestamp(): Long? = networkUser?.lastSignInTimestamp?.toLongDate()

    override fun getCreationTimestamp(): Long? = networkUser?.createTimestamp?.toLongDate()

    override fun isAdmin(): Boolean? = networkUser?.isAdmin

    override fun getUid(): String? = networkUser?.userId

    override fun getDisplayName(): String? = networkUser?.fullName

    override fun getPhotoUrl(): Uri? = networkUser?.photoUrl?.toUri()
}