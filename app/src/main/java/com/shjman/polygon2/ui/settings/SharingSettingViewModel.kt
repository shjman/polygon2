package com.shjman.polygon2.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.TrustedUser
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharingSettingViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _onAddTrustedUserClicked = MutableSharedFlow<Unit>()
    val onAddTrustedUserClicked = _onAddTrustedUserClicked.asSharedFlow()

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    private val _onSendInviteLinkButtonClicked = MutableSharedFlow<String>()
    val onSendInviteLinkButtonClicked = _onSendInviteLinkButtonClicked.asSharedFlow()

    val trustedUsers = MutableStateFlow<List<TrustedUser>?>(null)

    fun startObserveTrustedEmails() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spentRepository.getTrustedUsers(
                    onError = { launch { _onError.emit(it) } },
                )
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
        viewModelScope.launch {
            val documentPath = spentRepository.getDocumentPath(
                onError = { launch { _onError.emit(it) } },
            )
            _onSendInviteLinkButtonClicked.emit(documentPath)
        }
    }
}
