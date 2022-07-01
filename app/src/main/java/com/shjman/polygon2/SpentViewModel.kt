package com.shjman.polygon2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SpentViewModel(private val spentRepository: SpentRepository) : ViewModel() {

    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: MutableStateFlow<Boolean> = _isLoading

    fun onAmountSpentChanged(amountSpent: Int) {
        _amountSpent.value = amountSpent
    }

    fun onSaveButtonClicked() {
        Timber.d("save this spent amount  == ${amountSpent.value}")
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.saveSpentAmount(_amountSpent.value ?: 0)
                delay(2000)
//                Timber.d("aaa 2")
            }
//            Timber.d("aaa 3")
            _amountSpent.value = 0
            _isLoading.value = false
        }
    }
}