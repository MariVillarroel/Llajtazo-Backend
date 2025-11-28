package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "estadisticas_evento_diarias")
data class DailyEventStats(

    @EmbeddedId
    val id: DailyEventStatsId,

    @Column(name = "visitas")
    val visitas: Int = 0,

    @Column(name = "tickets_vendidos")
    val ticketsVendidos: Int = 0
)


