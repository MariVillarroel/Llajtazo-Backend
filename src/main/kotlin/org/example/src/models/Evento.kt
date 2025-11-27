package org.example.src.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.LocalDate
import java.time.LocalTime
@MappedSuperclass
public abstract class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id_evento: Int = 0
    abstract val organizador: Organizador
    abstract val fecha: LocalDate
    abstract val hora: LocalTime
    //val comentarios: MutableList<Comentario>
    abstract val lugar: Location
   // val tags: List<Categoria>

    abstract fun getInfo(): String
    //fun agregarComentario(comentario: Comentario)
    abstract fun mostrarUbicacion(): String
    abstract fun esEventoFuturo(): Boolean
}