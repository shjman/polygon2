package com.shjman.polygon2.ui.edit_spending

import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class EditSpendingViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    companion object {
        const val DELAY_DURATION = BuildConfig.testDelayDuration
    }

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories = _categories.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _showSpendingUpdated = MutableSharedFlow<Unit>()
    val showSpendingUpdated = _showSpendingUpdated.asSharedFlow()

    private val _spending = MutableStateFlow<Spending?>(null)
    val spending = _spending.asStateFlow()

    fun onSpentAmountChanged(spentAmount: Int) {
        _spending.value = _spending.value?.copy(spentAmount = spentAmount)
    }

    fun onNoteChanged(note: String) {
        _spending.value = _spending.value?.copy(note = note)
    }

    fun onSelectedCategoryChanged(category: Category) {
        _spending.value = _spending.value?.copy(category = category)
    }

    fun onSaveButtonClicked() {
        launchCatching {
            _isLoading.value = true
            delay(DELAY_DURATION)
            withContext(Dispatchers.IO) {
                _spending.value?.let {
                    spentRepository.updateSpending(
                        spending = it,
                        showSpendingUpdated = _showSpendingUpdated,
                    )
                }
            }
            _isLoading.value = false
        }
    }

    suspend fun loadSpending(localDateTime: LocalDateTime) {
        launchCatching {
            _isLoading.value = true
            awaitAll(
                async {
                    delay(DELAY_DURATION)
                    _spending.value = spentRepository.getSpending(
                        localDateTime,
                    )
                },
                async {
                    delay(DELAY_DURATION)
                    _categories.value = spentRepository.getCategories()
                }
            )
            _isLoading.value = false
        }
    }

    fun onSpendingDateChanged(newYear: Int, newMonth: Int, newDayOfMonth: Int) {
        _spending.value = _spending.value?.copy(
            date = _spending.value?.date
                ?.withYear(newYear)
                ?.withMonth(newMonth)
                ?.withDayOfMonth(newDayOfMonth) ?: throw Exception(
                "onSpendingDateChanged() wtf ? newYear = $newYear newMonth = $newMonth  newDayOfMonth = $newDayOfMonth"
            )
        )
    }

    fun onSpendingTimeChanged(newHour: Int, newMinute: Int) {
        _spending.value = _spending.value?.copy(
            date = _spending.value?.date
                ?.withHour(newHour)
                ?.withMinute(newMinute) ?: throw Exception(
                "onSpendingTimeChanged() wtf ? newHour = $newHour newMinute = $newMinute"
            )
        )
    }
}