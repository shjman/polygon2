package com.shjman.polygon2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SpentViewModel(private val spentRepository: SpentRepository) : ViewModel() {

    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    suspend fun getAllSpending(): List<Spending> {
        delay(1000)
        return spentRepository.getAllSpending()
    }

    suspend fun getAllCategories(): List<Category> {
        delay(1000)
        val allCategories = spentRepository.getAllCategories()
        allCategories.firstOrNull()?.let { _selectedCategory.value = it }
        return allCategories
    }
}