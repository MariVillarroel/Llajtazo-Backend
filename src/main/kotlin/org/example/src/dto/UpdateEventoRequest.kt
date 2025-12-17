package org.example.src.dto

import java.time.LocalDateTime

data class UpdateEventoRequest(
    val id: Int,

    val lugarId: Int? = null,
    val categoriaId: Int? = null,
    val organizadorId: Int? = null,

    val titulo: String? = null,
    val descripcion: String? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val coverUrl: String? = null,
    val estado: String? = null,
    val tipoEvento: String? = null
)