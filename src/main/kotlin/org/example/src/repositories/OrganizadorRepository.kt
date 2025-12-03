package org.example.src.repositories

import org.example.src.models.Organizador
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrganizadorRepository : JpaRepository<Organizador, Int> {

    fun findByCorreo(correo: String): Organizador?
    fun existsByCorreo(correo: String): Boolean
    fun existsByUsername(username: String): Boolean

    @Query("SELECT o FROM Organizador o WHERE o.username LIKE %:username%")
    fun buscarPorUsernameLike(@Param("username") username: String): List<Organizador>


    @Query("SELECT o FROM Organizador o WHERE SIZE(o.followers) >= :minSeguidores")
    fun findPopulares(@Param("minSeguidores") minSeguidores: Int): List<Organizador>
}

