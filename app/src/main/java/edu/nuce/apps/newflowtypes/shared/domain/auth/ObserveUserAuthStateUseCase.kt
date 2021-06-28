package edu.nuce.apps.newflowtypes.shared.domain.auth

import edu.nuce.apps.newflowtypes.di.ApplicationScope
import edu.nuce.apps.newflowtypes.di.IoDispatcher
import edu.nuce.apps.newflowtypes.shared.data.signin.AuthenticatedUserInfo
import edu.nuce.apps.newflowtypes.shared.data.signin.AuthenticatedUserInfoBasic
import edu.nuce.apps.newflowtypes.shared.data.signin.NetworkRegisteredUserInfo
import edu.nuce.apps.newflowtypes.shared.data.signin.datasources.AuthStateUserDataSource
import edu.nuce.apps.newflowtypes.shared.data.signin.datasources.RegisteredUserDataSource
import edu.nuce.apps.newflowtypes.shared.domain.FlowUseCase
import edu.nuce.apps.newflowtypes.shared.result.Result
import edu.nuce.apps.newflowtypes.shared.result.data
import edu.nuce.apps.newflowtypes.shared.util.cancelIfActive
import edu.nuce.apps.newflowtypes.util.tryOffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [FlowUseCase] that observes two data sources to generate an [AuthenticatedUserInfo]
 * that includes whether the user is registered (is an attendee).
 *
 * [AuthStateUserDataSource] provides general user information, like user IDs, while
 * [RegisteredUserDataSource] observes a different data source to provide a flag indicating
 * whether the user is registered.
 */
@Singleton
class ObserveUserAuthStateUseCase @Inject constructor(
    private val registeredUserDataSource: RegisteredUserDataSource,
    private val authStateUserDataSource: AuthStateUserDataSource,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Any, AuthenticatedUserInfo>(ioDispatcher) {

    private var observeUserRegisteredChangesJob: Job? = null

    // As a separate coroutine needs to listen for user registration changes and emit to the
    // flow, a callbackFlow is used
    private val authStateChanges = callbackFlow {
        authStateUserDataSource.getBasicUserInfo().collect { userResult ->
            // Cancel observing previous user registered changes
            observeUserRegisteredChangesJob.cancelIfActive()

            if (userResult is Result.Success) {
                if (userResult.data != null) {
                    processUserData(userResult.data)
                } else {
                    tryOffer(Result.Success(NetworkRegisteredUserInfo(null, false)))
                }
            } else {
                tryOffer(Result.Error(Exception("Network user error")))
            }
        }
        // Always wait for the flow to be closed. Specially important for tests.
        awaitClose { observeUserRegisteredChangesJob.cancelIfActive() }
    }
        .shareIn(externalScope, SharingStarted.WhileSubscribed())

    override fun execute(parameters: Any): Flow<Result<AuthenticatedUserInfo>> = authStateChanges

    private suspend fun ProducerScope<Result<AuthenticatedUserInfo>>.processUserData(
        userData: AuthenticatedUserInfoBasic
    ) {
        if (!userData.isSignedIn()) {
            userSignedOut(userData)
        } else if (userData.getUid() != null) {
            userSignedIn(userData.getUid()!!, userData)
        } else {
            tryOffer(Result.Success(NetworkRegisteredUserInfo(userData, false)))
        }
    }

    private suspend fun ProducerScope<Result<AuthenticatedUserInfo>>.userSignedIn(
        userId: String,
        userData: AuthenticatedUserInfoBasic
    ) {
        // Observing the user registration changes from another scope to able to listen
        // for this and updates to getBasicUserInfo() simultaneously
        observeUserRegisteredChangesJob = externalScope.launch(ioDispatcher) {
            // Start observing the user in Firestore to fetch the `registered` flag
            registeredUserDataSource.observeUserChanges(userId).collect { result ->
                val isRegisteredValue: Boolean? = result.data
                if (isRegisteredValue == true && userData.isSignedIn()) {
                    //TODO something when there's new user data and the user is an attendee
                }

                tryOffer(Result.Success(NetworkRegisteredUserInfo(userData, isRegisteredValue)))
            }
        }
    }

    private fun ProducerScope<Result<AuthenticatedUserInfo>>.userSignedOut(
        userData: AuthenticatedUserInfoBasic?
    ) {
        tryOffer(Result.Success(NetworkRegisteredUserInfo(userData, false)))
    }
}