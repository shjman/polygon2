package com.shjman.polygon2.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.data.Spending
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SpentViewModel(
    @VisibleForTesting
    val spentRepository: SpentRepository
    ) : ViewModel() {

    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _spendingsFlow: MutableStateFlow<List<Spending>?> = MutableStateFlow(null)
    val spendingsFlow: StateFlow<List<Spending>?> = _spendingsFlow

    private val _categoriesFlow = MutableStateFlow<List<Category>?>(null)
    val categoriesFlow = _categoriesFlow.asStateFlow()

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
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.saveSpentAmount(
                    spentAmount = _amountSpent.value ?: 0,
                    note = _note.value ?: "",
                    category = selectedCategoryFlow.value ?: Category.empty(),
                )
                delay(1500)
            }
            _amountSpent.value = 0
            _note.value = ""
            _isLoading.value = false
        }
    }

    fun onRemoveSpendingClicked(uuid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.removeSpending(uuid)
            }
            _isLoading.value = false
        }
    }

    fun startObserveSpendings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                spentRepository.getSpendingsFlow()
                    .map { it.sortedByDescending { spending -> spending.date } }
                    .onEach { _spendingsFlow.value = it }
                    .collect()
            }
        }
    }

    fun startObserveCategories() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var isSelectedCategorySet = false
                spentRepository.getCategoriesFlow()
                    .onEach { categories ->
                        when {
                            categories.isEmpty() -> {
                                spentRepository.saveCategory(Category.empty())
                                return@onEach
                            }
                            else -> _categoriesFlow.value = categories
                        }
                        if (!isSelectedCategorySet) {
                            _selectedCategoryFlow.value = spentRepository.getPopularCategory2()
                            isSelectedCategorySet = true
                            updatePopularCategory()
                        }
                    }
                    .collect()
            }
        }
    }

    private suspend fun updatePopularCategory() {
        var spendings = _spendingsFlow.value
        if (spendings.isNullOrEmpty()) {
            spendings = spentRepository.getSpendings()
        }
        val last15Spendings = spendings
            .sortedByDescending { spending -> spending.date }
            .take(15) // take/follow last fresh spendings
        val popularCategoryID = last15Spendings
            .groupBy { it.category.id }
            .maxByOrNull { it.value.size }
            ?.key
        popularCategoryID?.let { spentRepository.updatePopularCategoryID(it) }
    }
}