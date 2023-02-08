package com.shjman.polygon2.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isUserOwner: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()
    val userData = MutableStateFlow<FirebaseUser?>(null)

    suspend fun startObserveSettingData() {
        delay(BuildConfig.testDelayDuration)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userData.value = spentRepository.getCurrentUserData(
                    onError = { launch { _onError.emit(it) } },
                )
                spentRepository.isUserOwner(
                    onError = { launch { _onError.emit(it) } },
                ).onEach {
                    isUserOwner.value = it
                }.collect()
            }
        }
        isLoading.value = false // todo could be updated because isUserOwner ignores
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            spentRepository.signOut()
        }
    }

    fun onStopObserveSharedDatabaseClicked() {
        viewModelScope.launch {
            spentRepository.removeSharedDocumentPath(
                onError = { launch { _onError.emit(it) } },
            )
        }
    }
}