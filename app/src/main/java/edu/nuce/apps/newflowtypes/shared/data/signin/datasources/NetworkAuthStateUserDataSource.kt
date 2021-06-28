package edu.nuce.apps.newflowtypes.shared.data.signin.datasources

import edu.nuce.apps.newflowtypes.data.ApiService
import edu.nuce.apps.newflowtypes.data.model.User
import edu.nuce.apps.newflowtypes.di.ApplicationScope
import edu.nuce.apps.newflowtypes.di.IoDispatcher
import edu.nuce.apps.newflowtypes.shared.data.signin.AuthenticatedUserInfoBasic
import edu.nuce.apps.newflowtypes.shared.data.signin.NetworkUserInfo
import edu.nuce.apps.newflowtypes.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class NetworkAuthStateUserDataSource @Inject constructor(
    private val apiService: ApiService,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthStateUserDataSource {

    // lastUid can be potentially consumed and written from different threads
    // Making it thread safe with @Volatile
    @Volatile
    private var lastUid: String? = null

    private val basicUserInfo: SharedFlow<Result<AuthenticatedUserInfoBasic?>> =
        flow { emit(apiService.getAuthUserInfo(id = 1)) }
            .catch { e -> Result.Error(Exception(e)) }
            .flowOn(ioDispatcher)
            .map { authState ->
                // This map gets executed in the Flow's context
                processAuthState(authState)
            }
            .shareIn(
                scope = externalScope,
                replay = 1,
                started = SharingStarted.WhileSubscribed()
            )

    override fun getBasicUserInfo(): Flow<Result<AuthenticatedUserInfoBasic?>> = basicUserInfo

    private fun processAuthState(authUser: User): Result<AuthenticatedUserInfoBasic?> {
        lastUid = authUser.userId
        // Send the current user for observers
        return Result.Success(NetworkUserInfo(authUser))
    }
}