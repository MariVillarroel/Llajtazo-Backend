package org.example.src.repositories

import org.example.src.models.EstadisticaId
import org.example.src.models.EstadisticasEventoDiario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EstadisticasRepository : JpaRepository<EstadisticasEventoDiario, EstadisticaId> {

    fun findByEventoId(eventoId: Int): List<EstadisticasEventoDiario>
}