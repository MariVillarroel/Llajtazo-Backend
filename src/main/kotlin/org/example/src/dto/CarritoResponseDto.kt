package org.example.src.dto

import org.example.src.models.Currency
import org.example.src.models.EstadoCarrito

data class CarritoResponseDto(
    val carritoId: Int,
    val eventoId: Int,
    val asistenteId: Int,
    val estado: EstadoCarrito,
    val items: List<CarritoItemResponseDto>,
    val montoTotal: Double,
    val currency: Currency?
)