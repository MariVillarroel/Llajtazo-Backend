package org.example.src.dto

import java.time.LocalDateTime
import org.example.src.models.EventoEntity

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
        fun fromEntity(eventoEntity: EventoEntity): EventoResponse =
            EventoResponse(
                id = eventoEntity.id,
                titulo = eventoEntity.titulo,
                descripcion = eventoEntity.descripcion,
                startTime = eventoEntity.startTime,
                endTime = eventoEntity.endTime,
                estado = eventoEntity.estado,
                coverUrl = eventoEntity.coverUrl,
                organizadorId = eventoEntity.organizador?.id,
                organizadorNombre = eventoEntity.organizador?.getNombreCompleto(),
                lugarId = eventoEntity.lugar?.id,
                lugarNombre = eventoEntity.lugar?.name,
                lugarDireccion = eventoEntity.lugar?.address,
                categoriaId = eventoEntity.categoria?.id,
                categoriaNombre = eventoEntity.categoria?.nombre
            )
    }
}