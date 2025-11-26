package org.example.src.repositories

import org.example.src.models.Organizador
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganizadorRepository : JpaRepository<Organizador, Int> {
    fun findOrgByCorreo(correo: String): Organizador?
}
