package com.shjman.polygon2.ui.categories

import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.BuildConfig
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {
    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories: StateFlow<List<Category>?>
        get() = _categories.asStateFlow()

    private val _onAddNewCategoryClicked = MutableSharedFlow<Unit>()
    val onAddNewCategoryClicked
        get() = _onAddNewCategoryClicked.asSharedFlow()

    fun loadCategories() {
        launchCatching {
            delay(BuildConfig.testDelayDuration)
            spentRepository.getCategoriesFlow()
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