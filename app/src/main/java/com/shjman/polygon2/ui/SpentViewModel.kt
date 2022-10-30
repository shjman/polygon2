package com.shjman.polygon2.ui

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

class SpentViewModel(private val spentRepository: SpentRepository) : ViewModel() {

    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _allSpending: MutableStateFlow<List<Spending>> = MutableStateFlow(listOf())
    val allSpending: StateFlow<List<Spending>> = _allSpending

    private val _note: MutableLiveData<String> = MutableLiveData<String>("")
    val note: LiveData<String> = _note

    private val _selectedCategory: MutableLiveData<Category> = MutableLiveData<Category>(Category.empty())
    val selectedCategory: LiveData<Category> = _selectedCategory

    fun onAmountSpentChanged(amountSpent: Int) {
        _amountSpent.value = amountSpent
    }

    fun onNoteChanged(note: String) {
        _note.value = note
    }

    fun onSelectedCategoryChanged(category: Category) {
        _selectedCategory.value = category
    }

    fun onSaveButtonClicked() {
        Timber.d("save this spent amount  == ${amountSpent.value}")
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                spentRepository.saveSpentAmount(
                    spentAmount = _amountSpent.value ?: 0,
                    note = _note.value ?: "",
                    category = selectedCategory.value ?: Category.empty(),
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

    fun loadAllSpending() {
        spentRepository.getAllSpendingFlow()
            .map { it.sortedByDescending { spending -> spending.date } }
            .onEach { _allSpending.value = it }
            .launchIn(viewModelScope)
    }

    suspend fun getAllCategories(): List<Category> {
        Timber.e("aaaa getAllCategories()")
        delay(1500)
        val allCategories = spentRepository.getAllCategories()
        allCategories.firstOrNull()?.let { _selectedCategory.value = it }
        Timber.e("aaaa allCategories == $allCategories")
        return allCategories
    }
}