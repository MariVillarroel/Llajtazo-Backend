package org.example.src.repositories

import org.example.src.models.Suscripcion
import org.example.src.models.EstadoSuscripcion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SuscripcionRepository : JpaRepository<Suscripcion, Int> {

    @Query("SELECT s FROM Suscripcion s WHERE s.organizador.id = :organizadorId")
    fun findByOrganizadorId(@Param("organizadorId") organizadorId: Int): Suscripcion?

    @Query("SELECT s FROM Suscripcion s WHERE s.estado = 'ACTIVA'")
    fun findAllActivas(): List<Suscripcion>

    @Query("SELECT s FROM Suscripcion s WHERE s.estado = :estado")
    fun findAllByEstado(@Param("estado") estado: EstadoSuscripcion): List<Suscripcion>

    // ✅ NUEVO: Para actualizar estados expirados
    @Query("SELECT s FROM Suscripcion s WHERE s.estado = 'ACTIVA' AND s.fechaFin < :ahora")
    fun findActivasExpiradas(@Param("ahora") ahora: LocalDateTime): List<Suscripcion>

    @Query("SELECT s FROM Suscripcion s WHERE s.fechaFin BETWEEN :hoy AND :en7Dias")
    fun findAllPorVencer(
        @Param("hoy") hoy: LocalDateTime = LocalDateTime.now(),
        @Param("en7Dias") en7Dias: LocalDateTime = LocalDateTime.now().plusDays(7)
    ): List<Suscripcion>
}