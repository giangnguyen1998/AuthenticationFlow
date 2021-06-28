package edu.nuce.apps.newflowtypes.ui.signin

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.nuce.apps.newflowtypes.di.ApplicationScope
import edu.nuce.apps.newflowtypes.di.MainDispatcher
import edu.nuce.apps.newflowtypes.shared.data.prefs.PreferenceStorage
import edu.nuce.apps.newflowtypes.shared.domain.auth.ObserveUserAuthStateUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SignInViewModelDelegateModule {

    @Singleton
    @Provides
    fun provideSignInViewModelDelegate(
        dataSource: ObserveUserAuthStateUseCase,
        @MainDispatcher mainDispatcher: CoroutineDispatcher,
        @ApplicationScope applicationScope: CoroutineScope,
        preferenceStorage: PreferenceStorage
    ): SignInViewModelDelegate {
        return NetworkSignInViewModelDelegate(
            observableAuthStateUseCase = dataSource,
            mainDispatcher = mainDispatcher,
            applicationScope = applicationScope,
            preferenceStorage = preferenceStorage
        )
    }
}