package org.example.src.repositories


import org.example.src.models.Asistente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface AsistenteRepository : JpaRepository<Asistente, Int> {
    fun findByCorreo(correo: String): Asistente?
    fun existsByCorreo(correo: String): Boolean
    fun existsByUsername(username: String): Boolean
}