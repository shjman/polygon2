package com.shjman.polygon2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.ui.snackbar.SnackbarManager
import com.shjman.polygon2.ui.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel(private val logRepository: LogRepository) : ViewModel() {

    fun launchCatching(
        useSnackbar: Boolean = true,
        block: suspend CoroutineScope.() -> Unit,
    ) =
        viewModelScope.launch(
            context = CoroutineExceptionHandler { _, throwable ->
                if (useSnackbar) {
                    SnackbarManager.showMessage(throwable.toSnackbarMessage())
                }
                logRepository.logNonFatalCrash(throwable)
            },
            block = block,
        )
}