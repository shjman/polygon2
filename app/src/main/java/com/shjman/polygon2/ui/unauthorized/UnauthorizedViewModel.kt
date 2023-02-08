package com.shjman.polygon2.ui.unauthorized

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnauthorizedViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    private val _requestToSignIn = MutableSharedFlow<Unit>()
    val requestToSignIn = _requestToSignIn.asSharedFlow()

    suspend fun checkIsUserSignIn() {
        _isLoading.value = true
        delay(BuildConfig.testDelayDuration)
        _isUserLoggedIn.value = spentRepository.checkIsUserSignIn()
        _isLoading.value = false
    }

    suspend fun onSignInClicked() {
        _isLoading.value = true
        _requestToSignIn.emit(Unit)
    }

    fun updateDataAfterSuccessSignIn() {
        viewModelScope.launch {
            spentRepository.updateDataAfterSuccessSignIn(
                onError = { launch { _onError.emit(it) } },
            )
        }
    }
}