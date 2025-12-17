package org.example.src.dto

import java.time.LocalDate

data class EstadisticasUpdateRequest(
    val eventoId: Int,
    val dia: LocalDate,
    val visitas: Int? = null,
    val ticketsVendidos: Int? = null
)