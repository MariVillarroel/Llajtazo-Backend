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
        fun fromEntity(eventoBasico: org.example.src.models.EventoBasico): EventoResponse =
            EventoResponse(
                id = eventoBasico.id,
                titulo = eventoBasico.titulo,
                descripcion = eventoBasico.descripcion,
                startTime = eventoBasico.startTime,
                endTime = eventoBasico.endTime,
                estado = eventoBasico.estado,
                coverUrl = eventoBasico.coverUrl,
                organizadorId = eventoBasico.organizador?.id,
                organizadorNombre = eventoBasico.organizador?.getNombreCompleto(),
                lugarId = eventoBasico.lugar?.id,
                lugarNombre = eventoBasico.lugar?.name,
                lugarDireccion = eventoBasico.lugar?.address,
                categoriaId = eventoBasico.categoria?.id,
                categoriaNombre = eventoBasico.categoria?.nombre
            )
    }
}