package org.example.src.models

import jakarta.persistence.*

@Entity
@Table(name = "tickets", schema = "llajtazo")
class TipoTicket(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    var eventoEntity: EventoEntity,

    @Column(name = "nombre", nullable = false)
    var nombre: String,

    @Column(name = "precio", nullable = false)
    var precio: Double,

    @Column(name = "moneda")
    var moneda: String = "BOB",

    @Column(name = "cantidad_total", nullable = false)
    var cantidadTotal: Int,

    @Column(name = "cantidad_vendida")
    var cantidadVendida: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TipoTicket) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "TipoTicket(id=$id, nombre='$nombre', precio=$precio $moneda, total=$cantidadTotal, vendida=$cantidadVendida)"
    }
}
