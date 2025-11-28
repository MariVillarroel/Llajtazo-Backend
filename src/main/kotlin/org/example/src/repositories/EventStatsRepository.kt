package org.example.src.repositories

import org.example.src.models.DailyEventStats
import org.example.src.models.DailyEventStatsId
import org.example.src.models.Organizador
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EventStatsRepository : JpaRepository<DailyEventStats, DailyEventStatsId> {

    @Query("""
        SELECT d 
        FROM DailyEventStats d 
        WHERE d.id.eventoId = :eventoId 
        ORDER BY d.id.dia ASC
    """)
    fun getDailyStatsForEvent(eventoId: Int): List<DailyEventStats>
}

//@Repository
//interface OrganizerRepository : JpaRepository<Organizador, Int> {
    //@Query("SELECT o.esPremium FROM Organizer o WHERE o.id = :organizerId")
    //fun isOrganizerPremium(organizerId: Int): Boolean
//}