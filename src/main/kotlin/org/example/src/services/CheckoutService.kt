package org.example.src.services

import org.example.src.dto.TicketResponseDto
import org.example.src.models.EstadoCarrito
import org.example.src.models.EstadoTicket
import org.example.src.models.MetodoPago
import org.example.src.models.Ticket
import org.example.src.pagos.PagoTickets
import org.example.src.repositories.CarritoItemRepository
import org.example.src.repositories.CarritoRepository
import org.example.src.repositories.TicketRepository
import org.example.src.repositories.ZonaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
class CheckoutService(
    private val carritoRepository: CarritoRepository,
    private val carritoItemRepository: CarritoItemRepository,
    private val zonaRepository: ZonaRepository,
    private val ticketRepository: TicketRepository
) {

    @Transactional
    fun checkout(carritoId: Int, metodo: MetodoPago, referencia: String): List<TicketResponseDto> {

        val carrito = carritoRepository.findById(carritoId)
            .orElseThrow { NoSuchElementException("Carrito con id $carritoId no encontrado") }

        require(carrito.estado == EstadoCarrito.ABIERTO) {
            "No se puede hacer checkout de un carrito con estado ${carrito.estado}"
        }

        val items = carritoItemRepository.findByCarrito_Id(requireNotNull(carrito.id))

        require(items.isNotEmpty()) { "El carrito no tiene items" }

        // 1) Validar stock y calcular total
        var montoTotal = 0.0

        // Validación de moneda única (recomendada)
        val monedaBase = items.first().zona.currency
        require(items.all { it.zona.currency == monedaBase }) {
            "Todas las zonas del carrito deben tener la misma moneda"
        }

        items.forEach { item ->
            val zonaId = requireNotNull(item.zona.idZona) { "Zona sin id" }

            val zona = zonaRepository.findByIdZonaAndEstadoTrue(zonaId)
                ?: throw IllegalArgumentException("Zona $zonaId no existe o está inactiva")

            require(zona.cantidadTickets >= item.cantidad) {
                "Stock insuficiente en zona ${zona.nombre}. Disponible=${zona.cantidadTickets}, solicitado=${item.cantidad}"
            }

            montoTotal += (zona.price * item.cantidad)
        }

        // 2) Procesar pago mock
        val pago = PagoTickets(
            metodo = metodo,
            referencia = referencia,
            monto = montoTotal,
            moneda = monedaBase.name
        )

        require(pago.procesar()) { "Pago mock rechazado (referencia/monto inválido)" }

        // 3) Crear tickets + descontar stock
        val ticketsACrear = mutableListOf<Ticket>()

        items.forEach { item ->
            val zonaId = requireNotNull(item.zona.idZona)

            val zonaManaged = zonaRepository.findByIdZonaAndEstadoTrue(zonaId)
                ?: throw IllegalArgumentException("Zona $zonaId no existe o está inactiva")

            // doble validación antes de descontar (seguridad)
            require(zonaManaged.cantidadTickets >= item.cantidad) {
                "Stock insuficiente en zona ${zonaManaged.nombre} al confirmar checkout"
            }

            repeat(item.cantidad) {
                ticketsACrear += Ticket(
                    zona = zonaManaged,
                    estado = EstadoTicket.VENDIDO
                )
            }

            zonaManaged.cantidadTickets -= item.cantidad
        }

        val savedTickets = ticketRepository.saveAll(ticketsACrear)

        // 4) Marcar carrito como PAGADO (solo después del pago aprobado)
        carrito.estado = EstadoCarrito.PAGADO

        return savedTickets.map {
            TicketResponseDto(
                idTicket = requireNotNull(it.idTicket),
                zonaId = requireNotNull(it.zona.idZona),
                estado = it.estado.name
            )
        }
    }
}