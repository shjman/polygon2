package com.shjman.polygon2.ui.unauthorized

import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class UnauthorizedViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null)
    val isUserLoggedIn: StateFlow<Boolean?>
        get() = _isUserLoggedIn.asStateFlow()

    private val _requestToSignIn = MutableSharedFlow<Unit>()
    val requestToSignIn: SharedFlow<Unit>
        get() = _requestToSignIn.asSharedFlow()

    fun checkIsUserSignIn() {
        launchCatching {
            _isLoading.value = true
            delay(BuildConfig.testDelayDuration)
            _isUserLoggedIn.value = spentRepository.checkIsUserSignIn()
            _isLoading.value = false
        }
    }

    fun clearInitState() {
        _isUserLoggedIn.value = null
    }

    suspend fun onSignInClicked() {
        _isLoading.value = true
        _requestToSignIn.emit(Unit)
    }

    fun updateDataAfterSuccessSignIn() {
        launchCatching {
            spentRepository.updateDataAfterSuccessSignIn()
        }
    }
}