package org.example.src.models

import jakarta.persistence.*

@Entity
@Table(name = "zonas")
class Zona(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idZona: Int? = null,

    @Column(nullable = false)
    var nombre: String,

    @Column(name = "precio", nullable = false)
    var price: Double,

    @Column(name = "activo", nullable = false)
    var activo: Boolean,


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    var evento: EventoEntity,

    @Column(name = "cantidad_tickets", nullable = false)
    var cantidadTickets: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var currency: Currency
) {
    fun isSoldOut(): Boolean = cantidadTickets <= 0 || !activo
}
