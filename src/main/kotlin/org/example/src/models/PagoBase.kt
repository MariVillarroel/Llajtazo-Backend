package org.example.src.models
import jakarta.persistence.*
@MappedSuperclass
abstract class PagoBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    abstract val metodo: MetodoPago

    abstract val estado: EstadoPago

    open val fecha: java.time.LocalDateTime = java.time.LocalDateTime.now()

    abstract val referencia: String

    abstract fun validar(): Boolean

    abstract fun procesar(): Boolean

    abstract fun generarRecibo(): Map<String, Any>

    abstract fun estaCompletado(): Boolean

    abstract fun estaPendiente(): Boolean

    abstract fun estaFallido(): Boolean

    abstract fun resumen(): String

    abstract fun esMontoValido(): Boolean

}