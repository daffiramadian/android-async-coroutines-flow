package com.example.praktikum5_navigationcompose_245150200111040.data.remote

import com.example.praktikum5_navigationcompose_245150200111040.data.remote.dto.UserDto
import com.example.praktikum5_navigationcompose_245150200111040.data.remote.dto.UserResponse

class StudentRemoteDataSource(
    private val api: StudentApiService
) {
    suspend fun getStudents(): List<UserDto> {
        val response: UserResponse = api.getUsers()
        return response.users
    }

    suspend fun getStudent(id: Int): UserDto {
        return api.getUser(id)
    }

    suspend fun searchStudents(query: String): List<UserDto> {
        val response: UserResponse = api.searchUsers(query)
        return response.users
    }
}
