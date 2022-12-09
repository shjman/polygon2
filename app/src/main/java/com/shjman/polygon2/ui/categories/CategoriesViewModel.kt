package com.shjman.polygon2.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?> = _categories.asStateFlow()

    private val _onAddNewCategoryClicked = MutableSharedFlow<Unit>()
    val onAddNewCategoryClicked = _onAddNewCategoryClicked.asSharedFlow()

    fun loadCategories() {
        viewModelScope.launch {
            delay(1000)
            spentRepository.getAllCategories().let {
                _categories.value = it
            }
        }
    }

    fun addNewCategoryClicked() {
        viewModelScope.launch {
            _onAddNewCategoryClicked.emit(Unit)
        }
    }
}