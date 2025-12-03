package org.example.src.repositories

import org.example.src.models.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository : JpaRepository<Location, Int> {

    fun findByName(name: String): List<Location>
}
