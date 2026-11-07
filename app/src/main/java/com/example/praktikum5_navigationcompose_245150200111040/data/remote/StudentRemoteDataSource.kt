package com.example.praktikum5_navigationcompose_245150200111040.data.remote

import com.example.praktikum5_navigationcompose_245150200111040.data.remote.dto.UserDto

class StudentRemoteDataSource(
    private val api: StudentApiService
) {
    suspend fun getStudents(): List<UserDto> =
        api.getUsers(limit = 10).users

    suspend fun getStudent(id: Int): UserDto =
        api.getUser(id)

    suspend fun searchStudents(query: String): List<UserDto> =
        api.searchUsers(query).users
}
