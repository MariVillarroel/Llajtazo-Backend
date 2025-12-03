package org.example.src.dto

import java.time.LocalDateTime

data class CreateEventoRequest(
    val organizadorId: Int?,
    val lugarId: Int?,
    val categoriaId: Int?,
    val titulo: String,
    val descripcion: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val coverUrl: String?,
    val estado: String? = null
)