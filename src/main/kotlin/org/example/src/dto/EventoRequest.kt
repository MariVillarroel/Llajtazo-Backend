package org.example.src.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class EventoRequest(
    @field:NotBlank(message = "El título es requerido")
    @field:Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    val titulo: String,

    val descripcion: String? = null,

    @field:NotBlank(message = "La fecha de inicio es requerida")
    val startTime: LocalDateTime,

    @field:NotBlank(message = "La fecha de fin es requerida")
    val endTime: LocalDateTime,

    val coverUrl: String? = null,

    val estado: String = "PUBLISHED",

    val tipoEvento: String = "B", // B = Básico, P = Premium

    // Relaciones por ID
    val organizadorId: Int? = null,
    val lugarId: Int? = null,
    val categoriaId: Int? = null
)