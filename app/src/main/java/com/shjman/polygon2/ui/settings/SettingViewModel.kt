package com.shjman.polygon2.ui.settings

import com.google.firebase.auth.FirebaseUser
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    internal val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    internal val isUserObserveSomebody: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _signOutCompleted = MutableSharedFlow<Unit>()
    internal val signOutCompleted: SharedFlow<Unit> = _signOutCompleted.asSharedFlow()
    internal val userData = MutableStateFlow<FirebaseUser?>(null)

    init {
        launchCatching {
            withContext(Dispatchers.IO) {
                userData.value = spentRepository.getCurrentUserData()
                spentRepository.isUserObserveSomebody()
                    .onEach {
                        isUserObserveSomebody.value = it
                    }.collect()
            }
        }
        isLoading.value = false // todo could be updated because isUserOwner ignores
    }

    internal fun onSignOutClicked() {
        launchCatching {
            spentRepository.signOut()
            _signOutCompleted.emit(Unit)
        }
    }

    internal fun onStopObserveSharedDatabaseClicked() {
        launchCatching {
            spentRepository.removeSharedDocumentPath()
        }
    }
}
