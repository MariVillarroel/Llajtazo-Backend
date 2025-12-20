package org.example.src.dto
import org.example.src.models.MetodoPago

data class CheckoutRequestDto(
    val metodo: MetodoPago,
    val referencia: String
)