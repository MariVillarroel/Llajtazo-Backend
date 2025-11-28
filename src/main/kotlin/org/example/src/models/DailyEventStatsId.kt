package org.example.src.models

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDate

@Embeddable
data class DailyEventStatsId(
    @Column(name = "evento_id")
    val eventoId: Int,

    @Column(name = "dia")
    val dia: LocalDate
) : Serializable

