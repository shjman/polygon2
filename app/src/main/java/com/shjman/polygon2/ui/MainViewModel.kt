package com.shjman.polygon2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shjman.polygon2.repository.SpentRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val spentRepository: SpentRepository
) : ViewModel() {

    fun saveSharedDocumentPath(documentPath: String) {
        viewModelScope.launch {
            spentRepository.updateSharedDocumentPath(documentPath)
        }
    }
}