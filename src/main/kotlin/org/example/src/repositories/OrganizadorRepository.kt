package org.example.src.repositories

import org.example.src.models.Organizador
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrganizadorRepository : JpaRepository<Organizador, Int> {
    fun findByCorreo(correo: String): Organizador?

    @Query("SELECT COUNT(o) > 0 FROM Organizador o WHERE o.username = :username")
    fun existsByUsername(@Param("username") username: String): Boolean

    @Query("SELECT COUNT(o) > 0 FROM Organizador o WHERE o.correo = :correo")
    fun existsByCorreo(@Param("correo") correo: String): Boolean
}