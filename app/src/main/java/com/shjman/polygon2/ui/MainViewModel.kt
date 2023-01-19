package com.shjman.polygon2.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _showShackBar = MutableStateFlow<String?>(null)
    val showShackBar = _showShackBar.asStateFlow()

    fun showSnackBar(message: String) {
        _showShackBar.value = message
    }
}