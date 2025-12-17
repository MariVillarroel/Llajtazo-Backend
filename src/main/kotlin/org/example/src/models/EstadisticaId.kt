package org.example.src.models

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDate

@Embeddable
data class EstadisticaId(
    @Column(name = "evento_id")
    val eventoId: Int,

    @Column(name = "dia")
    val dia: LocalDate
) : java.io.Serializable
