package edu.nuce.apps.newflowtypes.ui.signin

import android.net.Uri
import edu.nuce.apps.newflowtypes.di.ApplicationScope
import edu.nuce.apps.newflowtypes.di.MainDispatcher
import edu.nuce.apps.newflowtypes.shared.data.prefs.PreferenceStorage
import edu.nuce.apps.newflowtypes.shared.data.signin.AuthenticatedUserInfo
import edu.nuce.apps.newflowtypes.shared.domain.auth.ObserveUserAuthStateUseCase
import edu.nuce.apps.newflowtypes.shared.result.Result
import edu.nuce.apps.newflowtypes.util.WhileViewSubscribed
import edu.nuce.apps.newflowtypes.util.tryOffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

enum class SignInNavigationAction {
    RequestSignIn, RequestSignOut
}

interface SignInViewModelDelegate {
    /**
     * Live updated value of the current network user
     */
    val userInfo: StateFlow<AuthenticatedUserInfo?>

    /**
     * Live updated value of the current network users image url
     */
    val currentUserImageUri: StateFlow<Uri?>

    /**
     * Emits Events when a sign-in event should be attempted or a dialog shown
     */
    val signInNavigationActions: Flow<SignInNavigationAction>

    /**
     * Emit an Event on performSignInEvent to request sign-in
     */
    suspend fun emitSignInRequest()

    /**
     * Emit an Event on performSignInEvent to request sign-out
     */
    suspend fun emitSignOutRequest()

    val userId: StateFlow<String?>

    /**
     * Returns the current user ID or null if not available.
     */
    val userIdValue: String?

    val isUserSignedIn: StateFlow<Boolean>

    val isUserSignedInValue: Boolean

    val isUserRegistered: StateFlow<Boolean>

    val isUserRegisteredValue: Boolean
}

internal class NetworkSignInViewModelDelegate @Inject constructor(
    observableAuthStateUseCase: ObserveUserAuthStateUseCase,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @ApplicationScope val applicationScope: CoroutineScope,
    preferenceStorage: PreferenceStorage
) : SignInViewModelDelegate {

    private val _signInNavigationActions = Channel<SignInNavigationAction>(Channel.CONFLATED)
    override val signInNavigationActions: Flow<SignInNavigationAction> = _signInNavigationActions.receiveAsFlow()

    private val currentAuthUser: Flow<Result<AuthenticatedUserInfo?>> =
        preferenceStorage.accessToken.flatMapLatest { token ->
            when(token.isEmpty()) {
                true -> flowOf(Result.Success(null))
                else -> observableAuthStateUseCase(Any()).map {
                    if (it is Result.Error) {
                        Timber.e(it.exception)
                    }
                    it
                }
            }
        }

    override val userInfo: StateFlow<AuthenticatedUserInfo?> = currentAuthUser.map {
        (it as? Result.Success)?.data
    }.stateIn(applicationScope, WhileViewSubscribed, null)

    override val currentUserImageUri: StateFlow<Uri?> = userInfo.map {
        it?.getPhotoUrl()
    }.stateIn(applicationScope, WhileViewSubscribed, null)

    override suspend fun emitSignInRequest(): Unit = withContext(mainDispatcher) {
        _signInNavigationActions.tryOffer(SignInNavigationAction.RequestSignIn)
    }

    override suspend fun emitSignOutRequest(): Unit = withContext(mainDispatcher) {
        _signInNavigationActions.tryOffer(SignInNavigationAction.RequestSignOut)
    }

    override val userId: StateFlow<String?> = userInfo.mapLatest {
        it?.getUid()
    }.stateIn(applicationScope, WhileViewSubscribed, null)

    override val userIdValue: String? = userInfo.value?.getUid()

    override val isUserSignedIn: StateFlow<Boolean> = userInfo.map {
        it?.isSignedIn() ?: false
    }.stateIn(applicationScope, WhileViewSubscribed, false)

    override val isUserRegistered: StateFlow<Boolean> = userInfo.map {
        it?.isRegistered() ?: false
    }.stateIn(applicationScope, WhileViewSubscribed, false)

    override val isUserSignedInValue: Boolean = isUserSignedIn.value

    override val isUserRegisteredValue: Boolean = isUserRegistered.value
}