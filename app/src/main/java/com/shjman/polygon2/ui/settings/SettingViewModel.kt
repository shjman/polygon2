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

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isUserOwner: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isUserOwner = _isUserOwner.asStateFlow()

    private val _userData = MutableStateFlow<FirebaseUser?>(null)
    val userData = _userData.asStateFlow()

    suspend fun startObserveSettingData() {
        delay(BuildConfig.testDelayDuration)
        _userData.value = spentRepository.getCurrentUserData()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spentRepository.isUserOwner().onEach {
                    _isUserOwner.value = it
                }.collect()
            }
        }
        _isLoading.value = false // todo could be updated because isUserOwner ignores
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            spentRepository.signOut()
        }
    }

    fun onStopObserveSharedDatabaseClicked() {
        viewModelScope.launch {
            spentRepository.removeSharedDocumentPath()
        }
    }
}