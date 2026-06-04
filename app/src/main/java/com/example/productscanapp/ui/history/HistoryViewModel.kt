package com.example.productscanapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanapp.domain.ScanHistoryItem
import com.example.productscanapp.domain.ScanHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ScanHistoryRepository,
) : ViewModel() {

    val history: StateFlow<List<ScanHistoryItem>> = repository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun deleteFromHistory(barcode: String) {
        viewModelScope.launch {
            repository.deleteScan(barcode)
        }
    }
}

