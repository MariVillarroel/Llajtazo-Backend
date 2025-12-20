package org.example.src.repositories

import org.example.src.models.Carrito
import org.example.src.models.EstadoCarrito
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarritoRepository : JpaRepository<Carrito, Int> {

    fun findByAsistente_IdAndEvento_IdAndEstado(
        asistenteId: Int,
        eventoId: Int,
        estado: EstadoCarrito
    ): Carrito?

    fun findByAsistente_IdAndEvento_IdAndEstado(
        asistenteId: Int,
        eventoId: Int
    ): Carrito? = findByAsistente_IdAndEvento_IdAndEstado(asistenteId, eventoId, EstadoCarrito.ABIERTO)
}
