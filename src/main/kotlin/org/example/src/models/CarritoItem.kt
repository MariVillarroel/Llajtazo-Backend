package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "carrito_items", schema = "llajtazo")
class CarritoItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    val carrito: Carrito,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    val zona: Zona,

    @Column(nullable = false)
    var cantidad: Int,

    // snapshot
    @Column(name = "precio_unitario", nullable = false)
    var precioUnitario: Double,

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false)
    var currency: Currency,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() { updatedAt = LocalDateTime.now() }
}

