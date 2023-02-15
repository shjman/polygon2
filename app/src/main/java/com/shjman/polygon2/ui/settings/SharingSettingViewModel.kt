package com.shjman.polygon2.ui.settings

import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.TrustedUser
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharingSettingViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    private val _onAddTrustedUserClicked = MutableSharedFlow<Unit>()
    val onAddTrustedUserClicked: SharedFlow<Unit>
        get() = _onAddTrustedUserClicked.asSharedFlow()

    private val _onSendInviteLinkButtonClicked = MutableSharedFlow<String>()
    val onSendInviteLinkButtonClicked: SharedFlow<String>
        get() = _onSendInviteLinkButtonClicked.asSharedFlow()

    val trustedUsers = MutableStateFlow<List<TrustedUser>?>(null)

    fun startObserveTrustedEmails() {
        launchCatching {
            withContext(Dispatchers.IO) {
                spentRepository.getTrustedUsers()
                    .onEach { trustedUsers.value = it }
                    .collect()
            }
        }
    }

    fun addTrustedUserClicked() {
        viewModelScope.launch {
            _onAddTrustedUserClicked.emit(Unit)
        }
    }

    fun onSendInviteLinkButtonClicked() {
        launchCatching {
            val documentPath = spentRepository.getDocumentPath()
            _onSendInviteLinkButtonClicked.emit(documentPath)
        }
    }
}
