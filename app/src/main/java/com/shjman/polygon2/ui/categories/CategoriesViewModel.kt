package com.shjman.polygon2.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val spentRepository: SpentRepository,
) : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?>
        get() = _categories.asStateFlow()

    private val _onAddNewCategoryClicked = MutableSharedFlow<Unit>()
    val onAddNewCategoryClicked
        get() = _onAddNewCategoryClicked.asSharedFlow()

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    fun loadCategories() {
        viewModelScope.launch {
            delay(BuildConfig.testDelayDuration)
            spentRepository.getCategoriesFlow(
                onError = { launch { _onError.emit(it) } }
            )
                .onEach { _categories.value = it.minus(Category.empty()) }
                .launchIn(viewModelScope)
        }
    }

    fun addNewCategoryClicked() {
        viewModelScope.launch {
            _onAddNewCategoryClicked.emit(Unit)
        }
    }
}