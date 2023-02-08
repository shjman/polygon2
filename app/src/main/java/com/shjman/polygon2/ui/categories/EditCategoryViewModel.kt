package com.shjman.polygon2.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class EditCategoryViewModel(
    private val spentRepository: SpentRepository
) : ViewModel() {

    val categoryName = MutableStateFlow("")
    val isProceedButtonEnabled = MutableStateFlow(false)

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    private val _popBackStack = MutableSharedFlow<Unit>()
    val popBackStack = _popBackStack.asSharedFlow()

    fun categoryValueChanged(newValue: String) {
        categoryName.value = newValue
        isProceedButtonEnabled.value = newValue.trim().isNotBlank()
    }

    fun onDoneClicked() {
        isProceedButtonEnabled.value = false
        val category = Category(UUID.randomUUID().toString(), categoryName.value.trim())

        viewModelScope.launch {
            spentRepository.saveCategory(
                category = category,
                onError = { viewModelScope.launch { _onError.emit(it) } },
            )
            categoryName.value = ""
            _popBackStack.emit(Unit)
        }
    }

    fun onBackClicked() {
        categoryName.value = ""
        isProceedButtonEnabled.value = false
        viewModelScope.launch {
            _popBackStack.emit(Unit)
        }
    }
}