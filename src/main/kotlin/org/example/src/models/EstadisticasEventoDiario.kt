package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "estadisticas_evento_diario", schema = "llajtazo")
class EstadisticasEventoDiario(

    @EmbeddedId
    val id: EstadisticaId,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventoId") // 🔹 Usa el mismo id del evento como parte de la PK
    @JoinColumn(name = "evento_id", nullable = false)
    var evento: EventoEntity,

    @Column(name = "visitas", nullable = false)
    var visitas: Int = 0,

    @Column(name = "tickets_vendidos", nullable = false)
    var ticketsVendidos: Int = 0
) {
    override fun toString(): String {
        return "EstadisticasEventoDiario(eventoId=${id.eventoId}, dia=${id.dia}, visitas=$visitas, ticketsVendidos=$ticketsVendidos)"
    }
}