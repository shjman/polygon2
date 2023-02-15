package com.shjman.polygon2.ui.settings

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddTrustedUserViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    val isProceedButtonEnabled = MutableStateFlow(false)
    val trustedUserEmail = MutableStateFlow("")

    private val _popBackStack = MutableSharedFlow<Unit>()
    val popBackStack = _popBackStack.asSharedFlow()

    fun trustedUserEmailChanged(newValue: String) {
        trustedUserEmail.value = newValue
        isProceedButtonEnabled.value = newValue.trim().isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(newValue).matches()
    }

    fun onDoneClicked() {
        isProceedButtonEnabled.value = false

        launchCatching {
            spentRepository.addTrustedUser(
                trustedUserEmail = trustedUserEmail.value,
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