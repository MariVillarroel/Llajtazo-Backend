package org.example.services

import jakarta.persistence.EntityNotFoundException
import org.example.src.dto.*
import org.example.src.models.*
import org.example.src.repositories.SuscripcionRepository
import org.example.src.repositories.OrganizadorRepository
import org.example.src.services.SuscripcionService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

class SuscripcionServiceTest {

    private val suscripcionRepository: SuscripcionRepository = mock()
    private val organizadorRepository: OrganizadorRepository = mock()
    private val service = SuscripcionService(suscripcionRepository, organizadorRepository)

    @Test
    fun `procesar pago para nueva suscripcion exitoso`() {
        // Arrange
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 1,
            metodo = MetodoPago.TARJETA,  // Corregido
            periodoMeses = 12,
            tipoOperacion = TipoOperacionSuscripcion.NUEVA  // Usa el enum real
        )

        // Usa mock en lugar de crear instancia real
        val organizadorMock = mock(Organizador::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(username).thenReturn("org1")
            whenever(correo).thenReturn("org1@example.com")
            whenever(suscribed).thenReturn(false)
        }

        whenever(organizadorRepository.findById(1)).thenReturn(Optional.of(organizadorMock))
        whenever(suscripcionRepository.save(any())).thenAnswer { it.arguments[0] as Suscripcion }

        // Act
        val result = service.procesarPago(request)

        // Assert
        assertNotNull(result)
        assertEquals("PREMIUM", result.tipo)
        assertEquals("ACTIVA", result.estado)
        assertEquals(1, result.organizadorId)
        assertEquals(80.0, result.precioMensual)
        assertTrue(result.tienePremium)
        assertTrue(result.estaActiva)

        verify(organizadorRepository).save(organizadorMock)
        verify(suscripcionRepository).save(any())
    }

    @Test
    fun `procesar pago para renovacion exitoso`() {
        // Arrange
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 1,
            metodo = MetodoPago.PAYPAL,
            periodoMeses = 6,
            tipoOperacion = TipoOperacionSuscripcion.RENOVACION
        )

        val fechaInicioOriginal = LocalDateTime.now().minusMonths(12)
        val fechaFinOriginal = fechaInicioOriginal.plusMonths(12)
        val nuevaFechaFin = fechaFinOriginal.plusMonths(6) // +6 meses de renovación

        // Mock COMPLETO de Suscripcion (necesitas mockear todas las propiedades)
        val suscripcionMock = mock(Suscripcion::class.java)

        // Configurar TODAS las propiedades necesarias
        `when`(suscripcionMock.id).thenReturn(1)
        `when`(suscripcionMock.tipo).thenReturn(TipoSuscripcion.PREMIUM)
        `when`(suscripcionMock.estado).thenReturn(EstadoSuscripcion.ACTIVA)
        `when`(suscripcionMock.precioMensual).thenReturn(80.0)
        `when`(suscripcionMock.periodoMesesUltimoPago).thenReturn(6) // Nuevo período
        `when`(suscripcionMock.fechaInicio).thenReturn(fechaInicioOriginal)
        `when`(suscripcionMock.fechaFin).thenReturn(nuevaFechaFin) // Fecha extendida

        // Propiedades calculadas
        `when`(suscripcionMock.diasRestantes).thenReturn(180L) // ~6 meses
        `when`(suscripcionMock.estaActiva).thenReturn(true)
        `when`(suscripcionMock.tienePremiumActivo).thenReturn(true)

        // Propiedades de pago (CRÍTICAS para fromSuscripcion)
        `when`(suscripcionMock.ultimaReferenciaPago).thenReturn("REF-RENEW-123")
        `when`(suscripcionMock.ultimoMontoPago).thenReturn(480.0) // 80 * 6
        `when`(suscripcionMock.ultimoMetodoPago).thenReturn(MetodoPago.PAYPAL)
        `when`(suscripcionMock.estadoUltimoPago).thenReturn(EstadoPago.COMPLETADO)

        // Mock de Organizador
        val organizadorMock = mock(Organizador::class.java)
        `when`(organizadorMock.id).thenReturn(1)
        `when`(organizadorMock.username).thenReturn("org1")
        `when`(organizadorMock.correo).thenReturn("org1@example.com")
        `when`(organizadorMock.suscribed).thenReturn(true)
        `when`(organizadorMock.suscripcion).thenReturn(suscripcionMock)

        // Mock de métodos del organizador que fromSuscripcion podría necesitar
        `when`(organizadorMock.puedeCrearEventos()).thenReturn(true)
        `when`(organizadorMock.eventosCreados).thenReturn(mutableListOf<EventoEntity>())

        // Configurar la relación inversa
        `when`(suscripcionMock.organizador).thenReturn(organizadorMock)

        `when`(organizadorRepository.findById(1)).thenReturn(Optional.of(organizadorMock))
        `when`(suscripcionRepository.save(any())).thenAnswer {
            val savedSuscripcion = it.arguments[0] as Suscripcion
            // Devolver el mock en lugar de lo guardado para controlar el resultado
            suscripcionMock
        }

        // Act
        val result = service.procesarPago(request)

        // Assert
        assertNotNull(result)
        assertEquals("PREMIUM", result.tipo)
        assertEquals("ACTIVA", result.estado)
        assertTrue(result.diasRestantes > 0)
        assertEquals(1, result.organizadorId)
        assertEquals("org1", result.organizadorUsername)
        assertEquals(480.0, result.ultimoPago?.monto) // 80 * 6
    }

    @Test
    fun `procesar pago con periodo invalido lanza excepcion`() {
        // Arrange
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 1,
            metodo = MetodoPago.TARJETA,
            periodoMeses = 50, // Más del máximo 36
            tipoOperacion = TipoOperacionSuscripcion.NUEVA
        )

        // Act & Assert
        val ex = assertThrows<IllegalArgumentException> {
            service.procesarPago(request)
        }

        assertEquals("El período debe estar entre 1 y 36 meses", ex.message)
    }

    @Test
    fun `procesar pago para organizador inexistente lanza excepcion`() {
        // Arrange
        val request = CrearPagoSuscripcionDTO(
            organizadorId = 99,
            metodo = MetodoPago.TARJETA,
            periodoMeses = 12,
            tipoOperacion = TipoOperacionSuscripcion.NUEVA
        )

        whenever(organizadorRepository.findById(99)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows<EntityNotFoundException> {
            service.procesarPago(request)
        }
    }

    @Test
    fun `cancelar suscripcion exitoso`() {
        // Arrange
        // Mock de Organizador
        val organizadorMock = mock(Organizador::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(username).thenReturn("org1")
            whenever(suscribed).thenReturn(true)
        }

        // Mock de Suscripcion
        val suscripcionMock = mock(Suscripcion::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(tipo).thenReturn(TipoSuscripcion.PREMIUM)
            // Configurar el estado inicial como ACTIVA
            whenever(estado).thenReturn(EstadoSuscripcion.ACTIVA)
            whenever(organizador).thenReturn(organizadorMock)

            // Mockea los métodos necesarios para fromSuscripcion
            whenever(fechaInicio).thenReturn(LocalDateTime.now().minusMonths(1))
            whenever(fechaFin).thenReturn(LocalDateTime.now().plusMonths(1))
            whenever(precioMensual).thenReturn(80.0)
            whenever(periodoMesesUltimoPago).thenReturn(1)
            whenever(ultimaReferenciaPago).thenReturn("REF-123")
            whenever(ultimoMontoPago).thenReturn(80.0)
            whenever(ultimoMetodoPago).thenReturn(MetodoPago.TARJETA)
            whenever(estadoUltimoPago).thenReturn(EstadoPago.COMPLETADO)

            // Mockear propiedades computadas
            whenever(estaActiva).thenReturn(true)
            whenever(tienePremiumActivo).thenReturn(true)
            whenever(diasRestantes).thenReturn(30L)

            // IMPORTANTE: Mockear el método cancelar()
            doAnswer {
                // Cuando se llame a cancelar(), cambiar el estado a CANCELADA
                whenever(estado).thenReturn(EstadoSuscripcion.CANCELADA)
                whenever(estaActiva).thenReturn(false)
                whenever(tienePremiumActivo).thenReturn(false)
                this
            }.whenever(this).cancelar()
        }

        whenever(suscripcionRepository.findByOrganizadorId(1)).thenReturn(suscripcionMock)
        whenever(suscripcionRepository.save(any())).thenAnswer { it.arguments[0] as Suscripcion }

        // También mockear el guardado del organizador
        whenever(organizadorRepository.save(any())).thenAnswer { it.arguments[0] as Organizador }

        // Mockear fromSuscripcion para devolver el DTO correcto
        val dtoMock = mock(SuscripcionResponseDTO::class.java).apply {
            whenever(estado).thenReturn("CANCELADA")
            whenever(estaActiva).thenReturn(false)
            whenever(tienePremium).thenReturn(false)
        }

        // Usar ArgumentCaptor para capturar la suscripción guardada
        val suscripcionCaptor = ArgumentCaptor.forClass(Suscripcion::class.java)

        // Act
        val result = service.cancelarSuscripcion(1)

        // Assert
        assertEquals("CANCELADA", result.estado)
        assertFalse(result.estaActiva)
        assertFalse(result.tienePremium)

        // Verificar que se llamó al método cancelar()
        verify(suscripcionMock).cancelar()

        // Verificar que se guardó la suscripción
        verify(suscripcionRepository).save(suscripcionCaptor.capture())

        // Verificar que se actualizó el organizador
        verify(organizadorMock).suscribed = false
        verify(organizadorRepository).save(organizadorMock)
    }
    @Test
    fun `cancelar suscripcion inexistente lanza excepcion`() {
        // Arrange
        whenever(suscripcionRepository.findByOrganizadorId(99)).thenReturn(null)

        // Act & Assert
        val ex = assertThrows<EntityNotFoundException> {
            service.cancelarSuscripcion(99)
        }

        assertEquals("No existe suscripción para el organizador 99", ex.message)
    }

    @Test
    fun `obtener por organizador id devuelve suscripcion si existe`() {
        // Arrange
        val suscripcionMock = mock(Suscripcion::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(tipo).thenReturn(TipoSuscripcion.PREMIUM)
            whenever(estado).thenReturn(EstadoSuscripcion.ACTIVA)
        }

        whenever(suscripcionRepository.findByOrganizadorId(1)).thenReturn(suscripcionMock)

        // Act
        val result = service.obtenerPorOrganizadorId(1)

        // Assert
        assertNotNull(result)
        assertEquals(1, result?.id)
        assertEquals(TipoSuscripcion.PREMIUM, result?.tipo)  // Propiedad directa
    }

    @Test
    fun `obtener por organizador id devuelve null si no existe`() {
        // Arrange
        whenever(suscripcionRepository.findByOrganizadorId(99)).thenReturn(null)

        // Act
        val result = service.obtenerPorOrganizadorId(99)

        // Assert
        assertNull(result)
    }

    @Test
    fun `tiene suscripcion activa devuelve true cuando existe y esta activa`() {
        // Arrange
        val suscripcionMock = mock(Suscripcion::class.java).apply {
            whenever(tipo).thenReturn(TipoSuscripcion.PREMIUM)
            whenever(estado).thenReturn(EstadoSuscripcion.ACTIVA)
            whenever(fechaFin).thenReturn(LocalDateTime.now().plusMonths(1))
            whenever(estaActiva).thenReturn(true)  // Propiedad calculada mockeada
        }

        whenever(suscripcionRepository.findByOrganizadorId(1)).thenReturn(suscripcionMock)

        // Act
        val result = service.tieneSuscripcionActiva(1)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `tiene suscripcion activa devuelve false cuando esta expirada`() {
        // Arrange
        val suscripcionMock = mock(Suscripcion::class.java).apply {
            whenever(tipo).thenReturn(TipoSuscripcion.PREMIUM)
            whenever(estado).thenReturn(EstadoSuscripcion.ACTIVA)
            whenever(fechaFin).thenReturn(LocalDateTime.now().minusDays(1))
            whenever(estaActiva).thenReturn(false)  // Propiedad calculada mockeada
        }

        whenever(suscripcionRepository.findByOrganizadorId(1)).thenReturn(suscripcionMock)

        // Act
        val result = service.tieneSuscripcionActiva(1)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `puede crear eventos devuelve true si organizador existe`() {
        // Arrange
        val organizadorMock = mock(Organizador::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(username).thenReturn("org1")
        }

        whenever(organizadorRepository.findById(1)).thenReturn(Optional.of(organizadorMock))

        // Act
        val result = service.puedeCrearEventos(1)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `puede crear eventos devuelve false si organizador no existe`() {
        // Arrange
        whenever(organizadorRepository.findById(99)).thenReturn(Optional.empty())

        // Act
        val result = service.puedeCrearEventos(99)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `actualizar estados expirados cambia estado a inactiva`() {
        // Arrange
        val organizador1Mock = mock(Organizador::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(username).thenReturn("org1")
        }

        val organizador2Mock = mock(Organizador::class.java).apply {
            whenever(id).thenReturn(2)
            whenever(username).thenReturn("org2")
        }

        // Suscripción expirada
        val suscripcionActivaExpirada = mock(Suscripcion::class.java).apply {
            whenever(id).thenReturn(1)
            whenever(estado).thenReturn(EstadoSuscripcion.ACTIVA)
            whenever(fechaFin).thenReturn(LocalDateTime.now().minusDays(1))
            whenever(organizador).thenReturn(organizador1Mock)
        }

        // Suscripción no expirada
        val suscripcionActivaNoExpirada = mock(Suscripcion::class.java).apply {
            whenever(id).thenReturn(2)
            whenever(estado).thenReturn(EstadoSuscripcion.ACTIVA)
            whenever(fechaFin).thenReturn(LocalDateTime.now().plusDays(1))
            whenever(organizador).thenReturn(organizador2Mock)
        }

        whenever(suscripcionRepository.findAll()).thenReturn(
            listOf(suscripcionActivaExpirada, suscripcionActivaNoExpirada)
        )

        // Act
        service.actualizarEstadosExpirados()

        // Assert
        // Verifica que se llamó a la propiedad estado
        verify(suscripcionActivaExpirada).estado = EstadoSuscripcion.INACTIVA
        verify(suscripcionActivaNoExpirada, never()).estado = any()
        verify(suscripcionRepository, times(1)).save(suscripcionActivaExpirada)
        verify(suscripcionRepository, never()).save(suscripcionActivaNoExpirada)
    }

    @Test
    fun `obtener dias restantes devuelve valor correcto`() {
        // Arrange
        val suscripcionMock = mock(Suscripcion::class.java).apply {
            whenever(diasRestantes).thenReturn(30L)  // Propiedad calculada
        }

        whenever(suscripcionRepository.findByOrganizadorId(1)).thenReturn(suscripcionMock)

        // Act
        val result = service.obtenerDiasRestantes(1)

        // Assert
        assertEquals(30L, result)
    }

    @Test
    fun `obtener dias restantes devuelve null si no hay suscripcion`() {
        // Arrange
        whenever(suscripcionRepository.findByOrganizadorId(99)).thenReturn(null)

        // Act
        val result = service.obtenerDiasRestantes(99)

        // Assert
        assertNull(result)
    }

    @Test
    fun `obtener plan actual devuelve plan correcto`() {
        // Arrange
        val suscripcionMock = mock(Suscripcion::class.java).apply {
            whenever(tipo).thenReturn(TipoSuscripcion.PREMIUM)
        }

        whenever(suscripcionRepository.findByOrganizadorId(1)).thenReturn(suscripcionMock)

        // Act
        val result = service.obtenerPlanActual(1)

        // Assert
        assertEquals("PREMIUM", result)
    }

    @Test
    fun `obtener plan actual devuelve GRATUITO si no hay suscripcion`() {
        // Arrange
        whenever(suscripcionRepository.findByOrganizadorId(99)).thenReturn(null)

        // Act
        val result = service.obtenerPlanActual(99)

        // Assert
        assertEquals("GRATUITO", result)
    }
}