package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "carritos", schema = "llajtazo")
class Carrito(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asistente_id", nullable = false)
    val asistente: Asistente,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    val evento: EventoEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var estado: EstadoCarrito = EstadoCarrito.ABIERTO,

    @OneToMany(mappedBy = "carrito", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<CarritoItem> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() { updatedAt = LocalDateTime.now() }
}
