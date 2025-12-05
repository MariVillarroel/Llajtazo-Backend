package org.example.src.models
import jakarta.persistence.*
import java.time.LocalDateTime

class PagoSuscripcion(
    @Enumerated(EnumType.STRING)
    override val metodo: MetodoPago,

    val periodoMeses: Int = 1,

    @Enumerated(EnumType.STRING)
    override var estado: EstadoPago = EstadoPago.PENDIENTE,

    override val fecha: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var tipoOperacion: TipoOperacionSuscripcion = TipoOperacionSuscripcion.RENOVACION

) : PagoBase() {

    companion object {
        const val PRECIO_MENSUAL: Double = 80.0
    }

    // Si PagoBase tiene id, no necesitas declararlo aquí
    // @Id @GeneratedValue ya está en PagoBase

    val monto: Double
        get() = PRECIO_MENSUAL * periodoMeses

    override val referencia: String = generarReferencia()

    fun calcularValidoHasta(): LocalDateTime = fecha.plusMonths(periodoMeses.toLong())

    override fun validar(): Boolean = esMontoValido() && estado == EstadoPago.PENDIENTE

    override fun procesar(): Boolean {
        estado = EstadoPago.PROCESANDO
        // Logica de procesamiento real
        estado = EstadoPago.COMPLETADO
        return true
    }

    override fun generarRecibo(): Map<String, Any> = mapOf(
        "id" to id,
        "monto" to monto,
        "precioMensual" to PRECIO_MENSUAL,
        "periodoMeses" to periodoMeses,
        "tipo" to "SUSCRIPCION",
        "operacion" to tipoOperacion.name,
        "referencia" to referencia,
        "fechaCompra" to fecha,
        "validoHasta" to calcularValidoHasta()
    )

    override fun estaCompletado(): Boolean = estado == EstadoPago.COMPLETADO
    override fun estaPendiente(): Boolean = estado == EstadoPago.PENDIENTE
    override fun estaFallido(): Boolean = estado == EstadoPago.FALLIDO

    override fun resumen(): String =
        "Pago #$id - $${"%.2f".format(monto)} (${periodoMeses} meses x $${PRECIO_MENSUAL}) - ${tipoOperacion}"

    private fun generarReferencia(): String = "SUB-${System.currentTimeMillis()}"

    override fun esMontoValido(): Boolean = monto >= 0.0 && periodoMeses > 0

    fun getValidoHastaAsLocalDate(): java.time.LocalDate = calcularValidoHasta().toLocalDate()

    fun getValidoHasta(): LocalDateTime = calcularValidoHasta()

    fun calcularMonto(periodoMeses: Int): Double = PRECIO_MENSUAL * periodoMeses
}