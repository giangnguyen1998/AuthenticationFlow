package edu.nuce.apps.newflowtypes.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.nuce.apps.newflowtypes.data.ApiService
import edu.nuce.apps.newflowtypes.shared.data.signin.datasources.AuthStateUserDataSource
import edu.nuce.apps.newflowtypes.shared.data.signin.datasources.NetworkAuthStateUserDataSource
import edu.nuce.apps.newflowtypes.shared.data.signin.datasources.NetworkRegisteredUserDataSource
import edu.nuce.apps.newflowtypes.shared.data.signin.datasources.RegisteredUserDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SignInModule {

    @Singleton
    @Provides
    fun provideRegisteredUserDataSource(
        apiService: ApiService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): RegisteredUserDataSource {
        return NetworkRegisteredUserDataSource(apiService, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideAuthStateUserDataSource(
        apiService: ApiService,
        @ApplicationScope externalScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AuthStateUserDataSource {
        return NetworkAuthStateUserDataSource(
            apiService = apiService,
            externalScope = externalScope,
            ioDispatcher = ioDispatcher
        )
    }
}