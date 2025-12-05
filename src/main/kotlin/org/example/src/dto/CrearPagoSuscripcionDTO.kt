package org.example.src.dto
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.example.src.models.MetodoPago
import org.example.src.models.TipoOperacionSuscripcion

data class CrearPagoSuscripcionDTO(
    @field:NotNull(message = "El ID del organizador es requerido")
    @field:Min(value = 1, message = "El ID del organizador debe ser mayor a 0")
    val organizadorId: Int,

    @field:NotNull(message = "El método de pago es requerido")
    val metodo: MetodoPago,

    @field:NotNull(message = "El período en meses es requerido")
    @field:Min(value = 1, message = "El período mínimo es 1 mes")
    @field:Max(value = 36, message = "El período máximo es 36 meses")
    val periodoMeses: Int,

    val tipoOperacion: TipoOperacionSuscripcion = TipoOperacionSuscripcion.NUEVA,
) {
    fun calcularMontoTotal(): Double {
        return 80.0 * periodoMeses
    }
}

enum class TipoOperacionSuscripcionDTO {
    NUEVA,
    RENOVACION,
    UPGRADE
}