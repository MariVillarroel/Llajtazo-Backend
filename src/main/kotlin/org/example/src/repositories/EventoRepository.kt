package org.example.src.repositories

import org.example.src.models.EventoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventoRepository : JpaRepository<EventoEntity, Int> {

    // Buscar eventos por organizador, ordenados por fecha de inicio descendente
    fun findByOrganizadorIdOrderByStartTimeDesc(organizadorId: Int): List<EventoEntity>

    // Buscar eventos futuros publicados, ordenados por fecha de inicio ascendente
    fun findByEndTimeGreaterThanEqualAndEstadoOrderByStartTimeAsc(
        endTime: LocalDateTime,
        estado: String
    ): List<EventoEntity>

    // Opcional: buscar eventos por categoría
    fun findByCategoriaIdOrderByStartTimeAsc(categoriaId: Int): List<EventoEntity>

    // Opcional: buscar eventos por lugar
    fun findByLugarIdOrderByStartTimeAsc(lugarId: Int): List<EventoEntity>
}