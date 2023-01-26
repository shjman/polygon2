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

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _trustedUsers = MutableStateFlow<List<TrustedUser>?>(null)
    val trustedUsers = _trustedUsers.asStateFlow()

    private val _onAddTrustedUserClicked = MutableSharedFlow<Unit>()
    val onAddTrustedUserClicked = _onAddTrustedUserClicked.asSharedFlow()

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
}