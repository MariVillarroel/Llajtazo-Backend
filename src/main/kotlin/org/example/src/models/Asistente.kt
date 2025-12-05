package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "usuarios")
class Asistente(
    final override var id: Int = 0,
    @Column(nullable = false)
    override var username: String,

    @Column(nullable = false, unique = true)
    final override var correo: String,

    @Column(nullable = false)
    final override var password: String,

    @Column(name="avatar_url")
    final override var profile_pic: String?="",

    @Column(name = "created_at", updatable = false)
    override var fechaCreacion: LocalDateTime = LocalDateTime.now()



) : User () {

    override fun get_Role(): UserRole = UserRole.ASISTENTE

    // Relación con organizadores seguidos
    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    val organizadoresSeguidos: MutableList<Organizador> = mutableListOf()

    // Relación con categorías/intereses
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tags",
        joinColumns = [JoinColumn(name = "usuario_id")],
        inverseJoinColumns = [JoinColumn(name = "categorias_id")]
    )


    // Métodos específicos de Asistente
    fun seguirOrganizador(organizador: Organizador) {
        if (!organizadoresSeguidos.contains(organizador)) {
            organizadoresSeguidos.add(organizador)
            organizador.agregarSeguidor(this)
        }
    }

    fun dejarDeSeguirOrganizador(organizador: Organizador) {
        organizadoresSeguidos.remove(organizador)
        organizador.removerSeguidor(this)
    }

    fun agregarInteres(categoria: Categoria) {
        if (!tags.contains(categoria)) {
            tags.add(categoria)
        }
    }

    fun totalOrganizadoresSeguidos(): Int = organizadoresSeguidos.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Asistente
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

