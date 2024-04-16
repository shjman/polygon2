package com.shjman.polygon2.ui.overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class OverviewViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    var isLoading by mutableStateOf(false)
        private set

    private val _spendingsFlow: MutableStateFlow<List<Spending>?> = MutableStateFlow(null)
    val spendingsFlow: StateFlow<List<Spending>?> = _spendingsFlow

    fun onRemoveSpendingClicked(uuid: String) {
        launchCatching {
            isLoading = true
            delay(5000)
            withContext(Dispatchers.IO) {
                spentRepository.removeSpending(
                    uuid = uuid,
                )
            }
            isLoading = false
        }
    }

    fun startObserveSpendings() {
        launchCatching {
            withContext(Dispatchers.IO) {
                delay(BuildConfig.testDelayDuration)
                spentRepository.getSpendingsFlow()
                    .map { it.sortedByDescending { spending -> spending.date } }
                    .onEach { _spendingsFlow.value = it }
                    .launchIn(viewModelScope)
            }
        }
    }
}