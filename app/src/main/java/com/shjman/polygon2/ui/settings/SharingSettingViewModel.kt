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

    private val _trustedUsers = MutableStateFlow<List<TrustedUser>?>(null)
    val trustedUsers = _trustedUsers.asStateFlow()

    private val _onAddTrustedUserClicked = MutableSharedFlow<Unit>()
    val onAddTrustedUserClicked = _onAddTrustedUserClicked.asSharedFlow()

    private val _onSendInviteLinkButtonClicked = MutableSharedFlow<Unit>()
    val onSendInviteLinkButtonClicked = _onSendInviteLinkButtonClicked.asSharedFlow()

    fun startObserveTrustedEmails() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spentRepository.getTrustedUsers()
                    .onEach { _trustedUsers.value = it }
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
            _onSendInviteLinkButtonClicked.emit(Unit)
        }
    }
}
