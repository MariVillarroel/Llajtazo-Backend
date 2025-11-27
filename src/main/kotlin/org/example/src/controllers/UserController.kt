package org.example.src.controllers

import org.example.src.dto.UserRegisterRequest
import org.example.src.dto.UserResponse
import org.example.src.services.UserService

class UserController(
    private val userService: UserService
) {

    fun register(request: UserRegisterRequest): UserResponse {
        return userService.registerUser(request)
    }
}