package org.example.src.repositories

import org.example.src.models.Evento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventoRepository : JpaRepository<Evento, Int> {

    fun findByOrganizadorIdOrderByStartTimeDesc(organizadorId: Int): List<Evento>

    fun findByEndTimeGreaterThanEqualAndEstadoOrderByStartTimeAsc(
        endTime: LocalDateTime,
        estado: String = "PUBLISHED"
    ): List<Evento>
}
