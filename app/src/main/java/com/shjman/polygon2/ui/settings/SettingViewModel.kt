package com.shjman.polygon2.ui.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userData = MutableStateFlow<FirebaseUser?>(null)
    val userData = _userData.asStateFlow()

    suspend fun startObserveSettingData() {
        delay(BuildConfig.testDelayDuration)
        _userData.value = spentRepository.getUserData()
        _isLoading.value = false
    }

    fun onSignOutClicked() {
        spentRepository.signOut()
    }
}