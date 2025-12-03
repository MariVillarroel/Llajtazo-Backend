package org.example.src.models

import jakarta.persistence.*

@Entity
@Table(name = "categorias", schema = "llajtazo")
data class Categoria(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,

    @Column(name = "nombre", nullable = false, unique = true)
    val nombre: String
)
