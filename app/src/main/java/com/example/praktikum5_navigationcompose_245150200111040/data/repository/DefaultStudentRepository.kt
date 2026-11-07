package com.example.praktikum5_navigationcompose_245150200111040.data.repository

import com.example.praktikum5_navigationcompose_245150200111040.data.model.Student
import com.example.praktikum5_navigationcompose_245150200111040.data.remote.StudentRemoteDataSource
import com.example.praktikum5_navigationcompose_245150200111040.data.remote.dto.toStudent

class DefaultStudentRepository(
    private val remoteDataSource: StudentRemoteDataSource
) : StudentRepository {

    override suspend fun getStudents(): List<Student> {
        return remoteDataSource.getStudents().map { it.toStudent() }
    }

    override suspend fun getStudent(id: Int): Student {
        return remoteDataSource.getStudent(id).toStudent()
    }

    override suspend fun searchStudents(query: String): List<Student> {
        return remoteDataSource.searchStudents(query).map { it.toStudent() }
    }
}
