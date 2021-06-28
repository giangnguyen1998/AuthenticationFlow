package edu.nuce.apps.newflowtypes.shared.domain.auth

import edu.nuce.apps.newflowtypes.data.ApiService
import edu.nuce.apps.newflowtypes.data.model.response.AuthUser
import edu.nuce.apps.newflowtypes.di.IoDispatcher
import edu.nuce.apps.newflowtypes.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Long, AuthUser>(ioDispatcher) {

    override suspend fun execute(parameters: Long): AuthUser {
        return apiService.loginUsers(parameters)
    }
}