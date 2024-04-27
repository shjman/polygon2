package com.shjman.polygon2.ui

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavHostController
import com.shjman.polygon2.ui.snackbar.SnackbarManager
import com.shjman.polygon2.ui.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class AppState(
    val coroutineScope: CoroutineScope,
    val navHostController: NavHostController,
    private val resources: Resources,
    val snackbarHostState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull()
                .collect { snackbarMessage ->
                    val text = snackbarMessage.toMessage(resources)
                    snackbarHostState.showSnackbar(text)
                }
        }
    }
}
