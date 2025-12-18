package org.example.src.repositories

import org.example.src.models.Asistente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AsistenteRepository : JpaRepository<Asistente, Int> {

    fun findByCorreo(correo: String): Asistente?
    fun existsByCorreo(correo: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): Asistente?

    @Query("SELECT a FROM Asistente a JOIN a.tags t WHERE t.id = :categoriaId")
    fun findByTagsId(@Param("categoriaId") categoriaId: Int): List<Asistente>

    @Query("SELECT a FROM Asistente a WHERE a.username LIKE %:nombre%")
    fun buscarPorNombre(@Param("nombre") nombre: String): List<Asistente>
}