package edu.nuce.apps.newflowtypes.shared.data.signin.datasources

import edu.nuce.apps.newflowtypes.shared.data.signin.AuthenticatedUserInfoBasic
import edu.nuce.apps.newflowtypes.shared.result.Result
import kotlinx.coroutines.flow.Flow

/**
 * Listens to an Authentication state data source that emits updates on the current user.
 *
 * @see NetworkRegisteredUserDataSource
 */
interface AuthStateUserDataSource {
    /**
     * Returns an observable of the [AuthenticatedUserInfoBasic].
     */
    fun getBasicUserInfo(): Flow<Result<AuthenticatedUserInfoBasic?>>
}