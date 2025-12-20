package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime
@Entity
@Table(name = "usuarios")
class Asistente(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Int = 0,

    @Column(nullable = false)
    override var username: String,

    @Column(name = "nombre_completo", nullable = false)
    var nombreCompleto: String,

    @Column(nullable = false, unique = true)
    override var correo: String,

    @Column(nullable = false)
    override var password: String,

    @Column(name="avatar_url")
    override var profile_pic: String? = null,

    @Column(name = "created_at", updatable = false)
    override var fechaCreacion: LocalDateTime = LocalDateTime.now()

) : User() {

    override fun get_Role(): UserRole = UserRole.ASISTENTE

    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    val organizadoresSeguidos: MutableList<Organizador> = mutableListOf()

    fun totalOrganizadoresSeguidos(): Int =
        organizadoresSeguidos.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Asistente) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
