package com.example.praktikum5_navigationcompose_245150200111040.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.praktikum5_navigationcompose_245150200111040.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadStudent(id: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, requestedId = id)
        viewModelScope.launch {
            try {
                val student = repository.getStudent(id)
                _uiState.value = DetailUiState(student = student, requestedId = id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    errorMessage = e.message ?: "Data tidak tersedia",
                    requestedId = id
                )
            }
        }
    }

    companion object {
        fun factory(repository: StudentRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(repository) as T
                }
            }
    }
}
