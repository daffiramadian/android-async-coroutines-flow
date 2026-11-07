package com.example.praktikum5_navigationcompose_245150200111040.ui.home

import com.example.praktikum5_navigationcompose_245150200111040.data.model.Student

data class HomeUiState(
    val students: List<Student> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
