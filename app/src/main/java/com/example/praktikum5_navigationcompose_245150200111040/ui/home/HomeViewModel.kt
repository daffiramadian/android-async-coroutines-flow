package com.example.praktikum5_navigationcompose_245150200111040.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.praktikum5_navigationcompose_245150200111040.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        observeSearch()
    }

    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        try {
            val trimmed = query.trim()
            val result = if (trimmed.isEmpty()) {
                repository.getStudents()
            } else {
                repository.searchStudents(trimmed)
            }
            _uiState.value = _uiState.value.copy(
                students = result,
                isLoading = false,
                errorMessage = null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                students = emptyList(),
                isLoading = false,
                errorMessage = e.message ?: "Terjadi kesalahan saat memuat data"
            )
        }
    }

    fun retry() {
        viewModelScope.launch {
            performSearch(_searchQuery.value)
        }
    }

    companion object {
        fun factory(repository: StudentRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}
