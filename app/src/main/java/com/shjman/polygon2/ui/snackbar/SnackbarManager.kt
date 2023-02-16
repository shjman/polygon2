package com.shjman.polygon2.ui.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val snackbarMessages: StateFlow<SnackbarMessage?>
        get() = messages.asStateFlow()

    fun showMessage(@StringRes messageInt: Int) {
        messages.value = SnackbarMessage.ResourceSnackbar(messageInt)
    }

    fun showMessage(message: SnackbarMessage) {
        messages.value = message
    }
}