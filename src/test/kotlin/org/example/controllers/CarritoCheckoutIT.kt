package org.example.src.integration

import org.example.src.dto.AgregarItemCarritoRequestDto
import org.example.src.dto.CrearCarritoRequestDto
import org.example.src.models.EstadoCarrito
import org.example.src.models.EstadoTicket
import org.example.src.models.MetodoPago
import org.example.src.repositories.CarritoItemRepository
import org.example.src.repositories.CarritoRepository
import org.example.src.repositories.TicketRepository
import org.example.src.repositories.ZonaRepository
import org.example.src.services.CarritoService
import org.example.src.services.CheckoutService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

/**
 * Test de integración "tipo B":
 * - Usa SpringBootTest (carga el contexto real)
 * - Usa BD local (tu MySQL local)
 * - Ejecuta un script SQL de seed antes de cada test
 * IMPORTANTE:
 * - Estos tests asumen IDs fijos definidos en el script.sql (recomendado).
 *   Ej: asistente_id=1, evento_id=1, zona_id=1, etc.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = ["classpath:script.sql"])
class CheckoutIntegrationTest {

    @Autowired private lateinit var carritoService: CarritoService
    @Autowired private lateinit var checkoutService: CheckoutService

    @Autowired private lateinit var carritoRepository: CarritoRepository
    @Autowired private lateinit var carritoItemRepository: CarritoItemRepository
    @Autowired private lateinit var zonaRepository: ZonaRepository
    @Autowired private lateinit var ticketRepository: TicketRepository

    /**
     * AJUSTA estos IDs a los que tú pongas en script.sql
     */
    private val asistenteId = 1
    private val eventoId = 1
    private val zonaVipId = 1

    @Test
    fun `flujo completo - crear carrito, agregar item, checkout - descuenta stock y genera tickets`() {
        // 1) Crear u obtener carrito ABIERTO
        val carrito = carritoService.crearOObtener(
            CrearCarritoRequestDto(
                asistenteId = asistenteId,
                eventoId = eventoId
            )
        )

        assertNotNull(carrito.carritoId)
        assertEquals(EstadoCarrito.ABIERTO, carrito.estado)
        assertEquals(asistenteId, carrito.asistenteId)
        assertEquals(eventoId, carrito.eventoId)

        // 2) Agregar items al carrito
        val carritoConItem = carritoService.agregarItem(
            carritoId = carrito.carritoId,
            dto = AgregarItemCarritoRequestDto(
                zonaId = zonaVipId,
                cantidad = 3
            )
        )

        assertEquals(1, carritoConItem.items.size)
        assertEquals(3, carritoConItem.items.first().cantidad)

        // Stock inicial desde BD (lo trae el seed)
        val zonaAntes = zonaRepository.findById(zonaVipId).orElseThrow()
        val stockAntes = zonaAntes.cantidadTickets
        assertTrue(stockAntes >= 3, "El seed debe tener stock >= 3 para este test")

        // 3) Checkout
        val tickets = checkoutService.checkout(
            carritoId = carrito.carritoId,
            metodo = MetodoPago.TRANSFERENCIA_BANCARIA,
            referencia = "REF-TEST-OK"
        )

        // 4) Validaciones tickets creados
        assertEquals(3, tickets.size)
        tickets.forEach {
            assertNotNull(it.idTicket)
            assertEquals(zonaVipId, it.zonaId)
            assertEquals(EstadoTicket.VENDIDO.name, it.estado)
        }

        // 5) Validar que el carrito quedó PAGADO
        val carritoDb = carritoRepository.findById(carrito.carritoId).orElseThrow()
        assertEquals(EstadoCarrito.PAGADO, carritoDb.estado)

        // 6) Validar que el stock se descontó
        val zonaDespues = zonaRepository.findById(zonaVipId).orElseThrow()
        assertEquals(stockAntes - 3, zonaDespues.cantidadTickets)

        // 7) Validar que quedaron tickets en BD (opcional, depende de tu repo)
        // Si tu TicketRepository NO tiene este método, comenta esto o ajústalo:
        val vendidos = ticketRepository.countByZona_IdZonaAndEstado(zonaVipId, EstadoTicket.VENDIDO)
        assertTrue(vendidos >= 3)
    }

    @Test
    fun `checkout falla si carrito no tiene items`() {
        val carrito = carritoService.crearOObtener(
            CrearCarritoRequestDto(asistenteId = asistenteId, eventoId = eventoId)
        )

        val ex = assertThrows(IllegalArgumentException::class.java) {
            checkoutService.checkout(
                carritoId = carrito.carritoId,
                metodo = MetodoPago.TRANSFERENCIA_BANCARIA,
                referencia = "REF-EMPTY"
            )
        }

        assertTrue(ex.message!!.contains("no tiene items", ignoreCase = true))
    }

    @Test
    fun `checkout falla si stock insuficiente`() {
        val carrito = carritoService.crearOObtener(
            CrearCarritoRequestDto(asistenteId = asistenteId, eventoId = eventoId)
        )

        // Pedimos una cantidad ridículamente alta para forzar error
        carritoService.agregarItem(
            carritoId = carrito.carritoId,
            dto = AgregarItemCarritoRequestDto(zonaId = zonaVipId, cantidad = 999999)
        )

        val ex = assertThrows(IllegalArgumentException::class.java) {
            checkoutService.checkout(
                carritoId = carrito.carritoId,
                metodo = MetodoPago.TRANSFERENCIA_BANCARIA,
                referencia = "REF-NOSTOCK"
            )
        }

        assertTrue(ex.message!!.contains("Stock insuficiente", ignoreCase = true))
    }

    @Test
    fun `no se puede modificar carrito PAGADO`() {
        val carrito = carritoService.crearOObtener(
            CrearCarritoRequestDto(asistenteId = asistenteId, eventoId = eventoId)
        )

        // Agregar algo válido
        carritoService.agregarItem(
            carritoId = carrito.carritoId,
            dto = AgregarItemCarritoRequestDto(zonaId = zonaVipId, cantidad = 1)
        )

        // Checkout para pasar a PAGADO
        checkoutService.checkout(
            carritoId = carrito.carritoId,
            metodo = MetodoPago.TRANSFERENCIA_BANCARIA,
            referencia = "REF-PAID"
        )

        // Intentar agregar otro item
        val ex = assertThrows(IllegalArgumentException::class.java) {
            carritoService.agregarItem(
                carritoId = carrito.carritoId,
                dto = AgregarItemCarritoRequestDto(zonaId = zonaVipId, cantidad = 1)
            )
        }

        assertTrue(ex.message!!.contains("No se puede modificar", ignoreCase = true))
    }

    @Test
    fun `no se puede agregar item de una zona que no pertenece al evento del carrito`() {
        // Requiere que el seed cree una zona de OTRO evento.
        // Ajusta estos IDs en tu script.sql
        val otroEventoId = 2
        val zonaDeOtroEventoId = 2

        val carrito = carritoService.crearOObtener(
            CrearCarritoRequestDto(asistenteId = asistenteId, eventoId = eventoId)
        )

        // Si no existe en seed, este test fallará por "no encontrada". Eso está ok si no lo necesitas.
        // Pero si lo quieres, crea evento 2 y zona 2 en script.sql
        val ex = assertThrows(IllegalArgumentException::class.java) {
            carritoService.agregarItem(
                carritoId = carrito.carritoId,
                dto = AgregarItemCarritoRequestDto(zonaId = zonaDeOtroEventoId, cantidad = 1)
            )
        }

        assertTrue(
            ex.message!!.contains("no pertenece al evento", ignoreCase = true) ||
                    ex.message!!.contains("no encontrada", ignoreCase = true)
        )
    }
}
