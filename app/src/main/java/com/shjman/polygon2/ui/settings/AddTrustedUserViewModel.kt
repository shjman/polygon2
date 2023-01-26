package com.shjman.polygon2.ui.settings

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTrustedUserViewModel(
    private val spentRepository: SpentRepository
) : ViewModel() {

    private val _isProceedButtonEnabled = MutableStateFlow(false)
    val isProceedButtonEnabled = _isProceedButtonEnabled.asStateFlow()

    private val _trustedUserEmail = MutableStateFlow("")
    val trustedUserEmail = _trustedUserEmail.asStateFlow()

    private val _popBackStack = MutableSharedFlow<Unit>()
    val popBackStack = _popBackStack.asSharedFlow()

    fun trustedUserEmailChanged(newValue: String) {
        _trustedUserEmail.value = newValue
        _isProceedButtonEnabled.value = newValue.trim().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(newValue).matches();
    }

    fun onDoneClicked() {
        _isProceedButtonEnabled.value = false

        viewModelScope.launch {
            spentRepository.addTrustedUser(_trustedUserEmail.value)
            _trustedUserEmail.value = ""
            _popBackStack.emit(Unit)
        }
    }

    fun onBackClicked() {
        _trustedUserEmail.value = ""
        _isProceedButtonEnabled.value = false
        viewModelScope.launch {
            _popBackStack.emit(Unit)
        }
    }
}