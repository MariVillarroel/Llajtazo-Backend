package org.example.src.repositories

import jakarta.persistence.LockModeType
import org.example.src.models.Zona
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ZonaRepository : JpaRepository<Zona, Int> {

    fun findByIdZonaAndEstadoTrue(idZona: Int): Zona?

    fun findByEvento_Id(eventoId: Int): List<Zona>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select z from Zona z where z.idZona = :id and z.activo = true")
    fun findForUpdate(@Param("id") id: Int): Zona?

}
