package org.example.src.models

import jakarta.persistence.*

@MappedSuperclass
abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @get:Column(name = "nombre_completo", nullable = false, unique = true, length = 255)
    abstract var username: String

    @get:Column(name = "email", nullable = false, unique = true, length = 255)
    abstract var correo: String

    @get:Column(name = "password_hash", nullable = false, length = 50)
    abstract var password: String

    @get:Column(name = "avatar_url", columnDefinition = "TEXT", nullable = true)
    abstract var profile_pic: String?

    @Column(name = "created_at", nullable = false)
    open var fechaCreacion: java.time.LocalDateTime = java.time.LocalDateTime.now()

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


    open fun getNombreCompleto(): String {
        return this.username
    }

    open fun estaActivo(): Boolean = activo

    open fun desactivar() {
        activo = false
    }

    open fun activar() {
        activo = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as User

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}