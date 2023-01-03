package com.shjman.polygon2.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class EditCategoryViewModel(
    private val spentRepository: SpentRepository
) : ViewModel() {

    private val _isProceedButtonEnabled = MutableStateFlow(false)
    val isProceedButtonEnabled = _isProceedButtonEnabled as StateFlow<Boolean>

    private val _categoryName = MutableStateFlow("")
    val categoryName = _categoryName as StateFlow<String>

    private val _popBackStack = MutableSharedFlow<Unit>()
    val popBackStack = _popBackStack as SharedFlow<Unit>

    fun categoryValueChanged(newValue: String) {
        _categoryName.value = newValue
        _isProceedButtonEnabled.value = newValue.trim().isNotBlank()
    }

    fun onDoneClicked() {
        _isProceedButtonEnabled.value = false
        val category = Category(UUID.randomUUID().toString(), _categoryName.value.trim())

        viewModelScope.launch {
            spentRepository.saveCategory(category)
            _categoryName.value = ""
            _popBackStack.emit(Unit)
        }
    }

    fun onBackClicked() {
        _categoryName.value = ""
        _isProceedButtonEnabled.value = false
        viewModelScope.launch {
            _popBackStack.emit(Unit)
        }
    }
}