package org.example.src.dto

import org.example.src.models.Currency

data class ZonaResponseDto(
    val idZona: Int,
    val nombre: String,
    val price: Double,
    val estado: Boolean,
    val cantidadTickets: Int,
    val currency: Currency,
    val soldOut: Boolean
)