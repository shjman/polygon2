package com.shjman.polygon2.ui.settings

import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isUserOwner: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _signOutCompleted = MutableSharedFlow<Unit>()
    val signOutCompleted: SharedFlow<Unit>
        get() = _signOutCompleted.asSharedFlow()
    val userData = MutableStateFlow<FirebaseUser?>(null)

    suspend fun startObserveSettingData() {
        delay(BuildConfig.testDelayDuration)
        launchCatching {
            withContext(Dispatchers.IO) {
                userData.value = spentRepository.getCurrentUserData()
                spentRepository.isUserOwner()
                    .onEach {
                        isUserOwner.value = it
                    }.collect()
            }
        }
        isLoading.value = false // todo could be updated because isUserOwner ignores
    }

    fun onSignOutClicked() {
        launchCatching {
            spentRepository.signOut()
            _signOutCompleted.emit(Unit)
        }
    }

    fun onStopObserveSharedDatabaseClicked() {
        launchCatching {
            spentRepository.removeSharedDocumentPath()
        }
    }
}