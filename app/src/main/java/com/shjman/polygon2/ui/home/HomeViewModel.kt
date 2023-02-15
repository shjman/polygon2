package com.shjman.polygon2.ui.home

import com.shjman.polygon2.repository.LogRepository
import com.shjman.polygon2.repository.SpentRepository
import com.shjman.polygon2.ui.BaseViewModel

class HomeViewModel(
    private val repository: SpentRepository, // todo resolve it
    logrepository: LogRepository,
) : BaseViewModel(logrepository)