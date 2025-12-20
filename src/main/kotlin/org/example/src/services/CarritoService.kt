package org.example.src.services

import org.example.src.dto.AgregarItemCarritoRequestDto
import org.example.src.dto.CarritoItemResponseDto
import org.example.src.dto.CarritoResponseDto
import org.example.src.dto.CrearCarritoRequestDto
import org.example.src.models.Carrito
import org.example.src.models.CarritoItem
import org.example.src.models.EstadoCarrito
import org.example.src.repositories.AsistenteRepository
import org.example.src.repositories.CarritoItemRepository
import org.example.src.repositories.CarritoRepository
import org.example.src.repositories.EventoRepository
import org.example.src.repositories.ZonaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
class CarritoService(
    private val carritoRepository: CarritoRepository,
    private val carritoItemRepository: CarritoItemRepository,
    private val asistenteRepository: AsistenteRepository,
    private val eventoRepository: EventoRepository,
    private val zonaRepository: ZonaRepository
) {

    /**
     * Crea (o devuelve) el carrito ABIERTO del asistente para un evento.
     */
    @Transactional
    fun crearOObtener(dto: CrearCarritoRequestDto): CarritoResponseDto {
        val asistente = asistenteRepository.findById(dto.asistenteId)
            .orElseThrow { NoSuchElementException("Asistente con id ${dto.asistenteId} no encontrado") }

        val evento = eventoRepository.findById(dto.eventoId)
            .orElseThrow { NoSuchElementException("Evento con id ${dto.eventoId} no encontrado") }

        val existente = carritoRepository
            .findByAsistente_IdAndEvento_IdAndEstado(dto.asistenteId, dto.eventoId, EstadoCarrito.ABIERTO)

        val carrito = existente ?: carritoRepository.save(
            Carrito(
                asistente = asistente,
                evento = evento,
                estado = EstadoCarrito.ABIERTO
            )
        )

        return buildCarritoResponse(carrito)
    }

    /**
     * Agrega (o incrementa) un item de carrito: zona + cantidad.
     * Copia snapshot de precio/moneda desde Zona al item.
     */
    @Transactional
    fun agregarItem(carritoId: Int, dto: AgregarItemCarritoRequestDto): CarritoResponseDto {
        require(dto.cantidad > 0) { "La cantidad debe ser mayor a 0" }

        val carrito = carritoRepository.findById(carritoId)
            .orElseThrow { NoSuchElementException("Carrito con id $carritoId no encontrado") }

        require(carrito.estado == EstadoCarrito.ABIERTO) {
            "No se puede modificar un carrito con estado ${carrito.estado}"
        }

        val zona = zonaRepository.findById(dto.zonaId)
            .orElseThrow { NoSuchElementException("Zona con id ${dto.zonaId} no encontrada") }

        require(zona.activo) { "Zona inactiva" }
        require(zona.evento.id == carrito.evento.id) { "La zona no pertenece al evento del carrito" }
        require(zona.cantidadTickets >= dto.cantidad) { "Stock insuficiente en la zona" }

        val itemExistente = carritoItemRepository.findByCarrito_IdAndZona_IdZona(carritoId, dto.zonaId)

        if (itemExistente != null) {
            val nuevaCantidad = itemExistente.cantidad + dto.cantidad
            require(zona.cantidadTickets >= nuevaCantidad) {
                "Stock insuficiente para la cantidad total en el carrito"
            }
            itemExistente.cantidad = nuevaCantidad
        }
        else {
            carritoItemRepository.save(
                CarritoItem(
                    carrito = carrito,
                    zona = zona,
                    cantidad = dto.cantidad,
                    precioUnitario = zona.price,
                    currency = zona.currency
                )
            )
        }

        return buildCarritoResponse(carrito)
    }


    /**
     * Obtiene el carrito con sus items + total calculado.
     */
    @Transactional(readOnly = true)
    fun obtener(carritoId: Int): CarritoResponseDto {
        val carrito = carritoRepository.findById(carritoId)
            .orElseThrow { NoSuchElementException("Carrito con id $carritoId no encontrado") }

        return buildCarritoResponse(carrito)
    }

    // ---------- Helpers de mapeo (en el Service, como pediste) ----------

    @Transactional(readOnly = true)
    private fun buildCarritoResponse(carrito: Carrito): CarritoResponseDto {
        val items = carritoItemRepository.findByCarrito_Id(requireNotNull(carrito.id))


        val itemsDto = items.map { it.toDto() }

        val montoTotal = items.sumOf { it.precioUnitario * it.cantidad }

        // Si el carrito tiene items, tomamos la moneda del primer item.
        // Si quieres permitir múltiples monedas, hay que cambiar el modelo.
        val currency = items.firstOrNull()?.currency

        return CarritoResponseDto(
            carritoId = requireNotNull(carrito.id),
            eventoId = carrito.evento.id,
            asistenteId = carrito.asistente.id,
            estado = carrito.estado,
            items = itemsDto,
            montoTotal = montoTotal,
            currency = currency
        )
    }

    private fun CarritoItem.toDto(): CarritoItemResponseDto =
        CarritoItemResponseDto(
            zonaId = requireNotNull(this.zona.idZona),
            nombreZona = this.zona.nombre,
            cantidad = this.cantidad,
            precioUnitario = this.precioUnitario,
            currency = this.currency,
            subtotal = this.precioUnitario * this.cantidad
        )

    @Transactional
    fun quitarItem(carritoId: Int, zonaId: Int): CarritoResponseDto {
        val carrito = carritoRepository.findById(carritoId)
            .orElseThrow { NoSuchElementException("Carrito con id $carritoId no encontrado") }

        require(carrito.estado == EstadoCarrito.ABIERTO) {
            "No se puede modificar un carrito con estado ${carrito.estado}"
        }

        val item = carritoItemRepository.findByCarrito_IdAndZona_IdZona(carritoId, zonaId)
            ?: throw NoSuchElementException("Item no encontrado para carrito=$carritoId zona=$zonaId")

        carritoItemRepository.delete(item)

        return buildCarritoResponse(carrito)
    }

}
