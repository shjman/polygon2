package com.shjman.polygon2.ui.unauthorized

import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.R
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import com.shjman.polygon2.ui.snackbar.SnackbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class UnauthorizedViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    private val _onShowError = MutableStateFlow(false)
    val onShowError: StateFlow<Boolean>
        get() = _onShowError.asStateFlow()

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
            try {
                _isUserLoggedIn.value = spentRepository.checkIsUserSignIn()
            } catch (ex: Exception) {
                _onShowError.value = true
                _isLoading.value = false
                throw ex
            }
            _isLoading.value = false
        }
    }

    fun setInitState() {
        _isUserLoggedIn.value = null
        _isLoading.value = true
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

    fun updateSharedDocumentPath(documentPath: String) {
        launchCatching {
            val ownerDocumentPath = spentRepository.getCurrentUserData()?.uid
            if (ownerDocumentPath == documentPath) {
                SnackbarManager.showMessage(R.string.own_self_observing)
            } else {
                spentRepository.updateSharedDocumentPath(documentPath)
            }
        }
    }
}