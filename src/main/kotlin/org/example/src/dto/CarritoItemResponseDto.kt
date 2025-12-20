package org.example.src.dto

import org.example.src.models.Currency

data class CarritoItemResponseDto(
    val zonaId: Int,
    val nombreZona: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val currency: Currency,
    val subtotal: Double
)