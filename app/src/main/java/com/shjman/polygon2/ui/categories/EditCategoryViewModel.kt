package com.shjman.polygon2.ui.categories

import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.data.Category
import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*

class EditCategoryViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    val categoryName = MutableStateFlow("")
    val isProceedButtonEnabled = MutableStateFlow(false)

    private val _popBackStack = MutableSharedFlow<Unit>()
    val popBackStack = _popBackStack.asSharedFlow()

    fun categoryValueChanged(newValue: String) {
        categoryName.value = newValue
        isProceedButtonEnabled.value = newValue.trim().isNotBlank()
    }

    fun onDoneClicked() {
        isProceedButtonEnabled.value = false
        val category = Category(UUID.randomUUID().toString(), categoryName.value.trim())

        launchCatching {
            spentRepository.saveCategory(
                category = category,
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