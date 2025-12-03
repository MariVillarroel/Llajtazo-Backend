package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "events", schema = "llajtazo")
class Evento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id")
    var organizador: Organizador? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id")
    var lugar: Location? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    var categoria: Categoria? = null,

    @Column(name = "titulo", nullable = false)
    var titulo: String,

    @Column(name = "descripcion")
    var descripcion: String? = null,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    var endTime: LocalDateTime,

    @Column(name = "cover_url")
    var coverUrl: String? = null,

    @Column(name = "estado")
    var estado: String = "PUBLISHED"
) {
    fun getInfo(): String =
        buildString {
            append(titulo)
            append(" - ")
            append(startTime.toLocalDate())
            append(" ")
            append(startTime.toLocalTime())
            lugar?.let {
                append(" @ ")
                append(it.name)
            }
        }

    fun mostrarUbicacion(): String =
        lugar?.let { "${it.name} - ${it.address ?: ""}" } ?: "Ubicación no definida"

    fun esEventoFuturo(now: LocalDateTime = LocalDateTime.now()): Boolean =
        endTime.isAfter(now)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Evento) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Evento(id=$id, titulo='$titulo', estado='$estado', fecha=${startTime.toLocalDate()})"
    }
}


