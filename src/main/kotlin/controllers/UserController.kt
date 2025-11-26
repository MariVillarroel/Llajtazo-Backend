package org.example.controllers

import org.example.dto.UserRegisterRequest
import org.example.dto.UserResponse
import org.example.services.UserService

class UserController(
    private val userService: UserService
) {

    fun register(request: UserRegisterRequest): UserResponse {
        return userService.registerUser(request)
    }
}