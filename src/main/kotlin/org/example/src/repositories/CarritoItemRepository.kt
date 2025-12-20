package org.example.src.repositories

import org.example.src.models.CarritoItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CarritoItemRepository : JpaRepository<CarritoItem, Int> {

    fun findByCarrito_Id(carritoId: Int): List<CarritoItem>

    fun findByCarrito_IdAndZona_IdZona(
        carritoId: Int,
        zonaId: Int
    ): CarritoItem?

    fun deleteByCarrito_Id(carritoId: Int)

    // Recomendado: traer zona en la misma query (evita N+1)
    @Query("""
        select ci from CarritoItem ci
        join fetch ci.zona z
        where ci.carrito.id = :carritoId
    """)
    fun findByCarritoIdFetchZona(@Param("carritoId") carritoId: Int): List<CarritoItem>
}
