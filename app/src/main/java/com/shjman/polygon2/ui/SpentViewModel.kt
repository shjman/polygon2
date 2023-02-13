package com.shjman.polygon2.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SpentViewModel(private val spentRepository: SpentRepository) : ViewModel() {

    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    private val _categoriesFlow = MutableStateFlow<List<Category>?>(null)
    val categoriesFlow = _categoriesFlow.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _note: MutableLiveData<String> = MutableLiveData<String>("")
    val note: LiveData<String> = _note

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

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
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.saveSpending(
                    category = selectedCategoryFlow.value ?: Category.empty(),
                    note = _note.value ?: "",
                    onError = { launch { _onError.emit(it) } },
                    spentAmount = _amountSpent.value ?: 0,
                )
            }
            _amountSpent.value = 0
            _note.value = ""
            _isLoading.value = false
        }
    }

    fun startObserveCategories() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(BuildConfig.testDelayDuration)
                var isSelectedCategorySet = false
                spentRepository.getCategoriesFlow(
                    onError = { launch { _onError.emit(it) } },
                )
                    .onEach { categories ->
                        when {
                            categories.isEmpty() -> {
                                spentRepository.saveCategory(
                                    Category.empty(),
                                    onError = { launch { _onError.emit(it) } },
                                )
                                return@onEach
                            }
                            else -> _categoriesFlow.value = categories
                        }
                        if (!isSelectedCategorySet) {
                            _selectedCategoryFlow.value = spentRepository.getPopularCategory(
                                onError = { launch { _onError.emit(it) } },
                            )
                            isSelectedCategorySet = true
                            updatePopularCategoryInStorage()
                        }
                    }
                    .collect()
            }
        }
    }

    private suspend fun updatePopularCategoryInStorage() {
        val spendings = spentRepository.getSpendings(
            onError = { viewModelScope.launch { _onError.emit(it) } },
        )
        val last15Spendings = spendings
            .sortedByDescending { spending -> spending.date }
            .take(15) // take/follow last fresh spendings
        val popularCategoryID = last15Spendings
            .groupBy { it.category.id }
            .maxByOrNull { it.value.size }
            ?.key
        popularCategoryID?.let {
            spentRepository.updatePopularCategoryID(
                onError = { viewModelScope.launch { _onError.emit(it) } },
                popularCategoryID = it,
            )
        }
    }
}