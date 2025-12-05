package org.example.models

import org.example.src.models.EstadoPago
import org.example.src.models.MetodoPago
import org.example.src.models.PagoSuscripcion
import org.example.src.models.TipoOperacionSuscripcion
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class PagoSuscripcionTest {

    @Test
    fun `crear pago suscripcion con valores validos`() {
        // Arrange & Act
        val pago = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
            periodoMeses = 12,
        )

        // Assert
        assertEquals(MetodoPago.TARJETA, pago.metodo)
        assertEquals(12, pago.periodoMeses)
        assertEquals(960.0, pago.monto) // 80.0 * 12
        assertEquals(EstadoPago.PENDIENTE, pago.estado)
        assertTrue(pago.referencia.startsWith("SUB-"))
    }

    @Test
    fun `procesar pago cambia estado a completado`() {
        // Arrange
        val pago = PagoSuscripcion(
            metodo = MetodoPago.PAYPAL,
        )

        // Act
        val resultado = pago.procesar()

        // Assert
        assertTrue(resultado)
        assertEquals(EstadoPago.COMPLETADO, pago.estado)
    }

    @Test
    fun `calcular valido hasta correctamente`() {
        // Arrange
        val fechaBase = LocalDateTime.of(2024, 1, 1, 0, 0)
        val pago = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
            periodoMeses = 3,
            fecha = fechaBase,
        )

        // Act
        val validoHasta = pago.calcularValidoHasta()

        // Assert
        assertEquals(fechaBase.plusMonths(3), validoHasta)
    }

    @Test
    fun `generar recibo con informacion completa`() {
        // Arrange
        val fecha = LocalDateTime.now()
        val pago = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
            periodoMeses = 6,
            fecha = fecha,
        )

        pago.id = 123

        // Act
        val recibo = pago.generarRecibo()

        // Assert
        assertEquals(123, recibo["id"])
        assertEquals(480.0, recibo["monto"]) // 80.0 * 6
        assertEquals(80.0, recibo["precioMensual"])
        assertEquals(6, recibo["periodoMeses"])
        assertEquals("SUSCRIPCION", recibo["tipo"])
        assertEquals(pago.referencia, recibo["referencia"])
    }

    @Test
    fun `validar pago con monto positivo y estado pendiente`() {
        // Arrange
        val pago = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
        )

        // Act
        val esValido = pago.validar()

        // Assert
        assertTrue(esValido)
    }

    @Test
    fun `esta completado devuelve true cuando estado es completado`() {
        // Arrange
        val pago = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
        )
        pago.estado = EstadoPago.COMPLETADO

        // Act
        val resultado = pago.estaCompletado()

        // Assert
        assertTrue(resultado)
    }

    @Test
    fun `calcular monto para diferentes periodos`() {
        // Arrange
        val pago1 = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
        )

        val pago6 = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
            periodoMeses = 6,
        )

        val pago12 = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
            periodoMeses = 12,
        )

        // Assert
        assertEquals(80.0, pago1.monto)
        assertEquals(480.0, pago6.monto)
        assertEquals(960.0, pago12.monto)
    }

    @Test
    fun `resumen muestra informacion correcta`() {
        // Arrange
        val pago = PagoSuscripcion(
            metodo = MetodoPago.TARJETA,
            periodoMeses = 12,
            tipoOperacion = TipoOperacionSuscripcion.NUEVA
        )

        // Act
        val resumen = pago.resumen()

        // Assert
        // Solo verifica lo esencial, no el formato exacto
        assertTrue(resumen.contains("960"), "Debe contener el monto total (960)")
        assertTrue(resumen.contains("12 meses"), "Debe contener el período")
        assertTrue(resumen.contains("80"), "Debe contener el precio mensual")
        assertTrue(resumen.contains("NUEVA"), "Debe contener el tipo de operación")

        // Verifica que tenga el formato básico
        assertTrue(resumen.startsWith("Pago #"), "Debe empezar con 'Pago #'")
        assertTrue(resumen.contains(" - "), "Debe usar ' - ' como separador")
    }
}
