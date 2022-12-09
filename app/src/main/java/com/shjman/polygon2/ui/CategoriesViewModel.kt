package com.shjman.polygon2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?> = _categories.asStateFlow()

    private val _addNewCategoryClicked = MutableStateFlow(Unit)
    val addNewCategoryClicked = _addNewCategoryClicked.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            delay(1000)
            spentRepository.getAllCategories().let {
                _categories.value = it
            }
        }
    }

    fun addNewCategoryClicked() {
    }
}