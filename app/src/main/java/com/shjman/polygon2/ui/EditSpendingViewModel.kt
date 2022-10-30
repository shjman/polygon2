package com.shjman.polygon2.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class EditSpendingViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _amountSpent = MutableStateFlow<Int?>(null)
    val amountSpent: StateFlow<Int?> = _amountSpent

    private val _showSpendingUpdated = MutableSharedFlow<Unit>()
    val showSpendingUpdated: SharedFlow<Unit> = _showSpendingUpdated

    private val _spending = MutableStateFlow<Spending?>(null)
    val spending: StateFlow<Spending?> = _spending

    private val _note: MutableLiveData<String> = MutableLiveData<String>("")
    val note: LiveData<String> = _note

    private val _selectedCategory: MutableLiveData<Category> = MutableLiveData<Category>(Category.empty())
    val selectedCategory: LiveData<Category> = _selectedCategory

    fun onAmountSpentChanged(amountSpent: Int?) {
        _amountSpent.value = amountSpent
        _spending.value = spending.value?.copy(
            spentAmount = amountSpent ?: 0
        )
    }

    fun onNoteChanged(note: String) {
        _note.value = note
    }

    fun onSelectedCategoryChanged(category: Category) {
        _selectedCategory.value = category
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spending.value?.let {
                    spentRepository.updateSpending(it, _showSpendingUpdated)
                }
            }
        }
    }

    suspend fun loadSpending(localDateTime: LocalDateTime) {
        _spending.value = spentRepository.getSpending(localDateTime)
        _amountSpent.value = spending.value?.spentAmount
    }

    fun onSpendingDateChanged(newYear: Int, newMonth: Int, newDayOfMonth: Int) {
        spending.value?.date?.let {
            _spending.value = spending.value?.copy(
                date = it.withYear(newYear).withMonth(newMonth).withDayOfMonth(newDayOfMonth)
            )
        }
    }

    fun onSpendingTimeChanged(newHour: Int, newMinute: Int) {
        spending.value?.date?.let {
            _spending.value = spending.value?.copy(
                date = it.withHour(newHour).withMinute(newMinute)
            )
        }
    }
}