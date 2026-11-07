package com.example.praktikum5_navigationcompose_245150200111040.data.repository

import com.example.praktikum5_navigationcompose_245150200111040.data.model.Student

interface StudentRepository {
    suspend fun getStudents(): List<Student>
    suspend fun getStudent(id: Int): Student
    suspend fun searchStudents(query: String): List<Student>
}
