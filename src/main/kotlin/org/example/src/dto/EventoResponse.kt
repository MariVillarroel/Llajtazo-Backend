package org.example.src.dto

import java.time.LocalDateTime

data class EventoResponse(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val estado: String,
    val coverUrl: String?,
    val organizadorId: Int?,
    val organizadorNombre: String?,
    val lugarId: Int?,
    val lugarNombre: String?,
    val lugarDireccion: String?,
    val categoriaId: Int?,
    val categoriaNombre: String?
) {
    companion object {
        fun fromEntity(evento: org.example.src.models.Evento): EventoResponse =
            EventoResponse(
                id = evento.id,
                titulo = evento.titulo,
                descripcion = evento.descripcion,
                startTime = evento.startTime,
                endTime = evento.endTime,
                estado = evento.estado,
                coverUrl = evento.coverUrl,
                organizadorId = evento.organizador?.id,
                organizadorNombre = evento.organizador?.getNombreCompleto(),
                lugarId = evento.lugar?.id,
                lugarNombre = evento.lugar?.name,
                lugarDireccion = evento.lugar?.address,
                categoriaId = evento.categoria?.id,
                categoriaNombre = evento.categoria?.nombre
            )
    }
}