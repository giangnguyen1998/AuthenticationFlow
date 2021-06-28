package edu.nuce.apps.newflowtypes.shared.data.signin.datasources

import edu.nuce.apps.newflowtypes.data.ApiService
import edu.nuce.apps.newflowtypes.di.IoDispatcher
import edu.nuce.apps.newflowtypes.shared.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class NetworkRegisteredUserDataSource @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val ioDisPatcher: CoroutineDispatcher
) : RegisteredUserDataSource {

    override fun observeUserChanges(userId: String): Flow<Result<Boolean?>> {
        return flow {
            val user = apiService.loginUsers(userId.toLong())
            if (user.data.email != null) {
                emit(Result.Success(true))
            } else {
                emit(Result.Success(false))
            }
        }
            .flowOn(ioDisPatcher)
            .catch { Result.Success(false) }
            .distinctUntilChanged()
    }
}