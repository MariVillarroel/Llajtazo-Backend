package org.example.src.models

import jakarta.persistence.*

@MappedSuperclass
abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Int = 0

    @get:Column(name = "username", nullable = false, unique = true, length = 50)
    abstract val username: String

    @get:Column(name = "correo", nullable = false, unique = true, length = 100)
    abstract val correo: String

    @get:Column(name = "password", nullable = false, length = 255)
    abstract val password: String

    @get:Column(name = "profile_pic", length = 500)
    abstract val profile_pic: String

    @Column(name = "fecha_creacion", nullable = false)
    open val fechaCreacion: java.time.LocalDateTime = java.time.LocalDateTime.now()

    @Column(name = "activo", nullable = false)
    open var activo: Boolean = true

    abstract fun get_Role(): String

    // Métodos comunes para todos los usuarios
    open fun getNombreCompleto(): String {
        return if (this is Organizador) {
            this.nombre_org
        } else {
            this.username
        }
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