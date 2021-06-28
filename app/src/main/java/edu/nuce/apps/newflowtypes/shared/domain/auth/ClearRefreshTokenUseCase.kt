package edu.nuce.apps.newflowtypes.shared.domain.auth

import edu.nuce.apps.newflowtypes.di.IoDispatcher
import edu.nuce.apps.newflowtypes.shared.data.prefs.PreferenceStorage
import edu.nuce.apps.newflowtypes.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ClearRefreshTokenUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(parameters: Unit) {
        preferenceStorage.clearRefreshToken()
    }
}