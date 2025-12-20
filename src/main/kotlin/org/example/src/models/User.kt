package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @get:Column(name = "username", nullable = false, length = 255)
    abstract var username: String

    @get:Column(name = "correo", nullable = false, unique = true, length = 255)
    abstract var correo: String

    @get:Column(name = "password", nullable = false, length = 255)
    abstract var password: String

    @get:Column(name = "profile_pic", nullable = true, length = 255)
    abstract var profile_pic: String?

    @Column(name = "created_at", nullable = false)
    open var fechaCreacion: LocalDateTime = LocalDateTime.now()

    @Transient
    open var activo: Boolean = true

    @ManyToMany
    @JoinTable(
        name = "tags",
        joinColumns = [JoinColumn(name = "usuario_id")],
        inverseJoinColumns = [JoinColumn(name = "categorias_id")]
    )
    val tags: MutableList<Categoria> = mutableListOf()

    abstract fun get_Role(): UserRole

    open fun estaActivo(): Boolean = activo
    open fun desactivar() { activo = false }
    open fun activar() { activo = true }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as User
        return id == other.id
    }

    override fun hashCode(): Int = id
}
