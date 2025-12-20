package org.example.src.repositories

import org.example.src.models.EstadoTicket
import org.example.src.models.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TicketRepository : JpaRepository<Ticket, Int> {

    fun findByZona_IdZona(zonaId: Int): List<Ticket>

    fun countByZona_IdZonaAndEstado(zonaId: Int, estado: EstadoTicket): Long

    // si necesitas listar tickets por evento (vía zona -> evento)
    @Query("""
        SELECT t FROM Ticket t
        WHERE t.zona.evento.id = :eventoId
    """)
    fun findByEventoId(@Param("eventoId") eventoId: Int): List<Ticket>
}
