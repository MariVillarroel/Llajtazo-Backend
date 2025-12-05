package org.example.src.models

enum class MetodoPago(
    val descripcion: String,
    val necesitaVerificacion: Boolean,
    val comisionPorcentaje: Double,
    val tiempoProcesamientoHoras: Int
) {
    TARJETA(
        descripcion = "Tarjeta de Crédito/Débito",
        necesitaVerificacion = true,
        comisionPorcentaje = 2.9,
        tiempoProcesamientoHoras = 0
    ),
    PAYPAL(
        descripcion = "PayPal",
        necesitaVerificacion = false,
        comisionPorcentaje = 3.4,
        tiempoProcesamientoHoras = 0
    ),
    TRANSFERENCIA_BANCARIA(
        descripcion = "Transferencia Bancaria",
        necesitaVerificacion = true,
        comisionPorcentaje = 0.0,
        tiempoProcesamientoHoras = 24
    );

    companion object {
        fun calcularComision(metodo: MetodoPago, monto: Double): Double {
            return monto * (metodo.comisionPorcentaje / 100)
        }

        fun metodosInstantaneos(): List<MetodoPago> {
            return entries.filter { it.tiempoProcesamientoHoras == 0 }
        }
    }
}
