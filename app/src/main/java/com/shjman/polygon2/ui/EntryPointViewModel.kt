package com.shjman.polygon2.ui

import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository

class EntryPointViewModel(
    private val spentRepository: SpentRepository,
    logRepository: LogRepository,
) : BaseViewModel(logRepository) {

    fun updateSharedDocumentPath(documentPath: String) {
        launchCatching {
            spentRepository.updateSharedDocumentPath(documentPath)
        }
    }
}