package com.shjman.polygon2.ui

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import com.shjman.polygon2.ui.snackbar.SnackbarManager
import com.shjman.polygon2.ui.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class AppState(
    val scaffoldState: ScaffoldState,
    private val resources: Resources,
    private val snackbarManager: SnackbarManager,
    val coroutineScope: CoroutineScope,
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull()
                .collect { snackbarMessage ->
                    val text = snackbarMessage.toMessage(resources)
                    scaffoldState.snackbarHostState.showSnackbar(text)
                }
        }
    }
}