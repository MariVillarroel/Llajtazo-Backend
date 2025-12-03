package org.example.src.repositories

import org.example.src.models.Categoria
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoriaRepository : JpaRepository<Categoria, Int> {

    fun findByNombre(nombre: String): Categoria?
}
