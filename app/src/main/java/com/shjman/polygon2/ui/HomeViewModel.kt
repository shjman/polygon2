package com.shjman.polygon2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {
    private val _showSnackbar = MutableSharedFlow<String>()
    val showSnackbar = _showSnackbar.asSharedFlow()

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    fun showSnackBar(message: String) {
        viewModelScope.launch {
            _showSnackbar.emit(message)
        }
    }

    fun isUserLoggedIn(it: Boolean) {
        _isUserLoggedIn.value = it
    }

    fun checkIsUserLoggedIn() {
        _isUserLoggedIn.value = spentRepository.checkIsUserLoggedIn()
    }
}