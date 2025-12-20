package org.example.src.models
import jakarta.persistence.*

@Entity
@Table(name = "tickets", schema = "llajtazo")
class Ticket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var idTicket: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    var zona: Zona,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var estado: EstadoTicket = EstadoTicket.DISPONIBLE,

    @Column(name = "detalles")
    var detalles: String? = null
)
