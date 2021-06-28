package edu.nuce.apps.newflowtypes.shared.domain.auth

import edu.nuce.apps.newflowtypes.di.IoDispatcher
import edu.nuce.apps.newflowtypes.shared.data.prefs.PreferenceStorage
import edu.nuce.apps.newflowtypes.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): UseCase<String, Unit>(ioDispatcher) {

    override suspend fun execute(parameters: String) {
        preferenceStorage.setAccessToken(parameters)
    }
}