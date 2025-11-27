package org.example.src.models

import jakarta.persistence.*

@Entity
@Table(name = "usuarios")
class Asistente(
    final override val id: Int,
    @Column(nullable = false, unique = true)
    override val username: String,

    @Column(nullable = false, unique = true)
    final override val correo: String,

    @Column(nullable = false)
    final override val password: String,

    @Column(name="profile_pic")
    final override var profile_pic: String=""

) : User () {

    override fun get_Role(): UserRole = UserRole.ASISTENTE

    // Relación con organizadores seguidos
    @ManyToMany(mappedBy = "followers", fetch = FetchType.LAZY)
    val organizadoresSeguidos: MutableList<Organizador> = mutableListOf()

    // Relación con categorías/intereses
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "asistente_categorias",
        joinColumns = [JoinColumn(name = "asistente_id")],
        inverseJoinColumns = [JoinColumn(name = "categoria_id")]
    )


    val tags: MutableList<Categoria> = mutableListOf()
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

