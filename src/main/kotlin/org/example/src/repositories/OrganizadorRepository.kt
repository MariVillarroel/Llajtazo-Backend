package org.example.src.repositories

import org.example.src.models.Organizador
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrganizadorRepository : JpaRepository<Organizador, Int> {

    fun findByUsername(username: String): Organizador?
    fun findByCorreo(correo: String): Organizador?
    fun existsByUsername(username: String): Boolean
    fun existsByCorreo(correo: String): Boolean

    @Query("SELECT o FROM Organizador o WHERE o.suscribed = :suscribed")
    fun findAllBySuscribed(@Param("suscribed") suscribed: Boolean): List<Organizador>

    @Query("""
        SELECT o FROM Organizador o 
        WHERE o.suscripcion IS NOT NULL 
        AND o.suscripcion.estado = 'ACTIVA'
    """)
    fun findAllConSuscripcionActiva(): List<Organizador>

    @Query("SELECT o FROM Organizador o WHERE o.username LIKE %:nombre%")
    fun buscarPorNombreOrganizacion(@Param("nombre") nombre: String): List<Organizador>

    @Query("SELECT o FROM Organizador o WHERE SIZE(o.followers) >= :minSeguidores")
    fun findPopulares(@Param("minSeguidores") minSeguidores: Int): List<Organizador>
}