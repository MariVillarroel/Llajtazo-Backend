package org.example.src.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int = 0
    abstract val username: String
    abstract val correo : String
    abstract val password: String
    abstract val profile_pic: String

    abstract fun get_Role(): String
}