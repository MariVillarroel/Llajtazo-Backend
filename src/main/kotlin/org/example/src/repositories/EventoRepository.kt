package org.example.src.repositories

import org.example.src.models.EventoBasico
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventoRepository : JpaRepository<EventoBasico, Int> {

    fun findByOrganizadorIdOrderByStartTimeDesc(organizadorId: Int): List<EventoBasico>

    fun findByEndTimeGreaterThanEqualAndEstadoOrderByStartTimeAsc(
        endTime: LocalDateTime,
        estado: String = "PUBLISHED"
    ): List<EventoBasico>
}
