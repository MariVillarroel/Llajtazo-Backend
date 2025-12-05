package org.example.src.models

enum class TipoSuscripcion(
    val descripcion: String,
    val precioMensual: Double
) {
    GRATUITO(
        descripcion = "Plan Básico Gratuito",
        precioMensual = 0.0
    ),
    PREMIUM(
        descripcion = "Plan Premium",
        precioMensual = 80.0
    );

    companion object {
        fun fromString(value: String): TipoSuscripcion? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
        fun requierePago(tipo: TipoSuscripcion): Boolean {
            return tipo != GRATUITO
        }
    }
}