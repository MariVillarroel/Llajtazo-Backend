package org.example.src.controllers

import org.example.src.dto.AgregarItemCarritoRequestDto
import org.example.src.dto.CarritoResponseDto
import org.example.src.dto.CheckoutRequestDto
import org.example.src.dto.CrearCarritoRequestDto
import org.example.src.dto.TicketResponseDto
import org.example.src.models.MetodoPago
import org.example.src.services.CarritoService
import org.springframework.web.bind.annotation.*
import org.example.src.services.CheckoutService

@RestController
@RequestMapping("/carritos")
class CarritoController(
    private val carritoService: CarritoService,
    private val checkoutService: CheckoutService,
    private val carrierService: CarritoService
) {

    /**
     * Crea (o devuelve) el carrito ABIERTO para (asistente, evento)
     * Body: { "asistenteId": 1, "eventoId": 10 }
     */
    @PostMapping
    fun crearOObtener(@RequestBody dto: CrearCarritoRequestDto): CarritoResponseDto =
        carritoService.crearOObtener(dto)

    /**
     * Obtiene el carrito con items y total calculado
     */
    @GetMapping("/{carritoId}")
    fun obtener(@PathVariable carritoId: Int): CarritoResponseDto =
        carritoService.obtener(carritoId)

    /**
     * Agrega (o incrementa) una zona dentro del carrito
     * Body: { "zonaId": 5, "cantidad": 2 }
     */
    @PostMapping("/{carritoId}/items")
    fun agregarItem(
        @PathVariable carritoId: Int,
        @RequestBody dto: AgregarItemCarritoRequestDto
    ): CarritoResponseDto =
        carritoService.agregarItem(carritoId, dto)

    @DeleteMapping("/{carritoId}/items/{zonaId}")
    fun quitarItem(
        @PathVariable carritoId: Int,
        @PathVariable zonaId: Int
    ): CarritoResponseDto = carritoService.quitarItem(carritoId, zonaId)

    @PostMapping("/{carritoId}/checkout")
    fun checkout(
        @PathVariable carritoId: Int,
        @RequestBody dto: CheckoutRequestDto
    ): List<TicketResponseDto> =
        checkoutService.checkout(
            carritoId = carritoId,
            metodo = dto.metodo,
            referencia = dto.referencia
        )

}
