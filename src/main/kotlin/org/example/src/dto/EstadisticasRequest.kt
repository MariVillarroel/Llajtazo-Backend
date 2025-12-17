package org.example.src.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class EstadisticasRequest(
    @field:NotNull(message = "El ID del evento es requerido")
    val eventoId: Int,

    @field:NotNull(message = "El día es requerido")
    val dia: LocalDate,

    val visitas: Int = 0,
    val ticketsVendidos: Int = 0
)