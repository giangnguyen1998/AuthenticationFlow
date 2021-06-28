package edu.nuce.apps.newflowtypes.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.nuce.apps.newflowtypes.shared.domain.auth.*
import edu.nuce.apps.newflowtypes.shared.result.Result
import edu.nuce.apps.newflowtypes.ui.signin.SignInNavigationAction
import edu.nuce.apps.newflowtypes.ui.signin.SignInViewModelDelegate
import edu.nuce.apps.newflowtypes.util.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MainNavigateActions {
    NavigateToHome,
    NavigateToLogin
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    networkStatusTracker: NetworkStatusTracker,
    private val loginUserUseCase: LoginUserUseCase,
    private val clearAccessTokenUseCase: ClearAccessTokenUseCase,
    private val clearRefreshTokenUseCase: ClearRefreshTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val signInViewModelDelegate: SignInViewModelDelegate
) : ViewModel(), SignInViewModelDelegate by signInViewModelDelegate {

    private val _navigateActions = Channel<MainNavigateActions>(Channel.CONFLATED)
    val navigateActions = _navigateActions.receiveAsFlow()

    private val _errorMessage = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val errorMessage = _errorMessage.receiveAsFlow()

    private val loginUseCaseResult = flow {
        emit(loginUserUseCase(parameters = 1))
    }.onEach {
        if (it is Result.Error) {
            when (val error = it.exception) {
                is NetworkErrorException -> _errorMessage.tryOffer(error.errorMessage)
                is AuthenticationException -> {
                    _errorMessage.tryOffer(error.errorMessage)
                    _navigateActions.tryOffer(MainNavigateActions.NavigateToLogin)
                }
            }
        }
    }.stateIn(
        viewModelScope,
        WhileViewSubscribed,
        Result.Loading
    )

    private val triggerLogin = signInNavigationActions.flatMapLatest { actions ->
        when (actions) {
            SignInNavigationAction.RequestSignIn -> {
                loginUseCaseResult.mapLatest {
                    if (it is Result.Success) {
                        setAccessTokenUseCase(it.data.accessToken)
                        setRefreshTokenUseCase(it.data.refreshToken)
                        _navigateActions.tryOffer(MainNavigateActions.NavigateToHome)
                    }
                    it
                }
            }
            SignInNavigationAction.RequestSignOut -> {
                clearAccessTokenUseCase(Unit)
                clearRefreshTokenUseCase(Unit)
                _navigateActions.tryOffer(MainNavigateActions.NavigateToLogin)
                flowOf(Result.Success(null))
            }
        }
    }.stateIn(
        viewModelScope,
        WhileViewSubscribed,
        null
    )

    init {
        viewModelScope.launch {
            triggerLogin.collect()
        }
    }

    val isLoading = triggerLogin.mapLatest {
        it == Result.Loading
    }.stateIn(
        viewModelScope,
        WhileViewSubscribed,
        false
    )

    val networkState = networkStatusTracker.networkStatus
        .map { status ->
            when (status) {
                NetworkStatus.Available -> NetworkState.Fetched
                NetworkStatus.Unavailable -> NetworkState.Failure
            }
        }

    fun signIn() {
        viewModelScope.launch {
            emitSignInRequest()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            emitSignOutRequest()
        }
    }
}