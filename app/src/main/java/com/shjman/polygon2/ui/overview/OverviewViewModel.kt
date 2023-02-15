package com.shjman.polygon2.ui.overview

import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _spendingsFlow: MutableStateFlow<List<Spending>?> = MutableStateFlow(null)
    val spendingsFlow: StateFlow<List<Spending>?> = _spendingsFlow

    fun onRemoveSpendingClicked(uuid: String) {
        launchCatching {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.removeSpending(
                    uuid = uuid,
                )
            }
            _isLoading.value = false
        }
    }

    fun startObserveSpendings() {
        launchCatching {
            withContext(Dispatchers.IO) {
                delay(BuildConfig.testDelayDuration)
                spentRepository.getSpendingsFlow()
                    .map { it.sortedByDescending { spending -> spending.date } }
                    .onEach { _spendingsFlow.value = it }
                    .collect()
            }
        }
    }
}