package com.shjman.polygon2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.time.LocalDateTime

class EditSpendingViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _spentAmount = MutableStateFlow(0)
    val amountSpent: StateFlow<Int> = _spentAmount

    private val _uuid = MutableStateFlow("")

    private val _showSpendingUpdated = MutableSharedFlow<Unit>()
    val showSpendingUpdated: SharedFlow<Unit> = _showSpendingUpdated

    private val _note = MutableStateFlow("null")
    val note: StateFlow<String> = _note

    private val _date = MutableStateFlow<LocalDateTime>(LocalDateTime.now())  // todo fix it
    val date: StateFlow<LocalDateTime> = _date

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?> = _categories

    private val _selectedCategory = MutableStateFlow(Category.empty())
    val selectedCategory: StateFlow<Category> = _selectedCategory

    fun onAmountSpentChanged(amountSpent: Int) {
        _spentAmount.value = amountSpent
    }

    fun onNoteChanged(note: String) {
        _note.value = note
    }

    fun onSelectedCategoryChanged(category: Category) {
        _selectedCategory.value = category
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            withContext(Dispatchers.IO) {
                Spending(
                    uuid = _uuid.value,
                    spentAmount = _spentAmount.value,
                    note = _note.value,
                    date = _date.value,
                    category = "",
                ).let {
                    spentRepository.updateSpending(it, _showSpendingUpdated)
                }
            }
            _isLoading.value = false
        }
    }

    suspend fun loadData(localDateTime: LocalDateTime) {
        viewModelScope.launch {
            _isLoading.value = true
            awaitAll(
                async {
                    spentRepository.getSpending(localDateTime)?.let {
                        delay(1000)
                        _uuid.value = it.uuid
                        _spentAmount.value = it.spentAmount
                        _note.value = it.note
                        _date.value = it.date
//                _selectedCategory = it.category todo fix
                    }
                },
                async {
                    spentRepository.getAllCategories().let {
                        delay(1000)
//                firstOrNull()?.let { _selectedCategory.value = it } // todo
                        _categories.value = listOf(Category("uuid", "test category"))
                        _selectedCategory.value = Category("uuid", "test category")
                    }
                }
            )
            _isLoading.value = false
        }
    }

    fun onSpendingDateChanged(newYear: Int, newMonth: Int, newDayOfMonth: Int) {
        _date.value = date.value
            .withYear(newYear)
            ?.withMonth(newMonth)
            ?.withDayOfMonth(newDayOfMonth) ?: throw Exception(
            "onSpendingDateChanged() wtf ? newYear = $newYear newMonth = $newMonth  newDayOfMonth = $newDayOfMonth"
        )
    }

    fun onSpendingTimeChanged(newHour: Int, newMinute: Int) {
        _date.value = date.value
            .withHour(newHour)
            ?.withMinute(newMinute) ?: throw Exception(
            "onSpendingTimeChanged() wtf ? newHour = $newHour newMinute = $newMinute"
        )
    }
}