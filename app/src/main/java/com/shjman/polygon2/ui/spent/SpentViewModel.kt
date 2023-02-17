package com.shjman.polygon2.ui.spent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber

class SpentViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    private val _categoriesFlow = MutableStateFlow<List<Category>?>(null)
    val categoriesFlow = _categoriesFlow.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _note: MutableLiveData<String> = MutableLiveData<String>("")
    val note: LiveData<String> = _note

    private val _selectedCategoryFlow = MutableStateFlow<Category?>(null)
    val selectedCategoryFlow: StateFlow<Category?> = _selectedCategoryFlow.asStateFlow()

    fun onAmountSpentChanged(amountSpent: Int) {
        _amountSpent.value = amountSpent
    }

    fun onNoteChanged(note: String) {
        _note.value = note
    }

    fun onSelectedCategoryChanged(category: Category) {
        _selectedCategoryFlow.value = category
    }

    fun onSaveButtonClicked() {
        Timber.d("save this spent amount  == ${amountSpent.value}")
        launchCatching {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.saveSpending(
                    category = selectedCategoryFlow.value ?: Category.empty(),
                    note = _note.value ?: "",
                    spentAmount = _amountSpent.value ?: 0,
                )
            }
            _amountSpent.value = 0
            _note.value = ""
            _isLoading.value = false
        }
    }

    fun startObserveCategories() {
        launchCatching {
            withContext(Dispatchers.IO) {
                delay(BuildConfig.testDelayDuration)
                var isSelectedCategorySet = false
                spentRepository.getCategoriesFlow()
                    .onEach { categories ->
                        when {
                            categories.isEmpty() -> {
                                spentRepository.saveCategory(
                                    Category.empty(),
                                )
                                return@onEach
                            }
                            else -> _categoriesFlow.value = categories
                        }
                        if (!isSelectedCategorySet) {
                            _selectedCategoryFlow.value = spentRepository.getPopularCategory()
                            isSelectedCategorySet = true
                            updatePopularCategoryInStorage()
                        }
                    }
                    .collect()
            }
        }
    }

    private fun updatePopularCategoryInStorage() {
        val myTrace = Firebase.performance.newTrace("updatePopularCategoryInStorage")
        myTrace.start()
        launchCatching {
            val spendings = spentRepository.getSpendings()
            val last15Spendings = spendings
                .sortedByDescending { spending -> spending.date }
                .take(15) // take/follow last fresh spendings
            val popularCategoryID = last15Spendings
                .groupBy { it.category.id }
                .maxByOrNull { it.value.size }
                ?.key
            popularCategoryID?.let {
                spentRepository.updatePopularCategoryID(
                    popularCategoryID = it,
                )
            }
        }
        myTrace.stop()
    }
}