package org.example.src.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "favoritos",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["usuario_id", "evento_id"])
    ]
)
@IdClass(FavoritoId::class)
data class Favorito(

    // usuario_id (PK)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties("favoritos")
    val asistente: Asistente,

    // evento_id (PK)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    @JsonIgnoreProperties("favoritos")
    val evento: Evento,

    // lo rellena MySQL con CURRENT_TIMESTAMP
    @Column(name = "creado_en", updatable = false, insertable = false)
    val createdAt: LocalDateTime? = null
)
