package com.shjman.polygon2.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel(private val spentRepository: SpentRepository) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    private val _spendingsFlow: MutableStateFlow<List<Spending>?> = MutableStateFlow(null)
    val spendingsFlow: StateFlow<List<Spending>?> = _spendingsFlow

    fun onRemoveSpendingClicked(uuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.removeSpending(
                    onError = { launch { _onError.emit(it) } },
                    uuid = uuid,
                )
            }
            _isLoading.value = false
        }
    }

    fun startObserveSpendings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(BuildConfig.testDelayDuration)
                spentRepository.getSpendingsFlow(
                    onError = { launch { _onError.emit(it) } },
                )
                    .map { it.sortedByDescending { spending -> spending.date } }
                    .onEach { _spendingsFlow.value = it }
                    .collect()
            }
        }
    }
}