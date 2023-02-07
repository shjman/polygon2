package com.shjman.polygon2.ui.settings

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddTrustedUserViewModel(
    private val spentRepository: SpentRepository
) : ViewModel() {

    val isProceedButtonEnabled = MutableStateFlow(false)
    val trustedUserEmail = MutableStateFlow("")

    private val _popBackStack = MutableSharedFlow<Unit>()
    val popBackStack = _popBackStack.asSharedFlow()

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    fun trustedUserEmailChanged(newValue: String) {
        trustedUserEmail.value = newValue
        isProceedButtonEnabled.value = newValue.trim().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(newValue).matches()
    }

    fun onDoneClicked() {
        isProceedButtonEnabled.value = false

        viewModelScope.launch {
            spentRepository.addTrustedUser(
                trustedUserEmail = trustedUserEmail.value,
                onError = { launch { _onError.emit(it) } },
            )
            trustedUserEmail.value = ""
            _popBackStack.emit(Unit)
        }
    }

    fun onBackClicked() {
        trustedUserEmail.value = ""
        isProceedButtonEnabled.value = false
        viewModelScope.launch {
            _popBackStack.emit(Unit)
        }
    }
}