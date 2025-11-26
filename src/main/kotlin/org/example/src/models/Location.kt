package org.example.src.models

data class Location (
    val id_location: Int,
    val name: String,
    val latitude: Double? = null,
    val altitude: Double? = null
)