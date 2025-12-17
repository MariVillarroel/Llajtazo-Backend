package org.example.src.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "events", schema = "llajtazo")
class EventoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id")
    var organizador: Organizador? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id")
    var lugar: Location? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    var categoria: Categoria? = null,

    @Column(nullable = false)
    var titulo: String,

    var descripcion: String? = null,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    var endTime: LocalDateTime,

    var coverUrl: String? = null,

    var estado: String = "PUBLISHED",

    val fechaCreacion: LocalDateTime = LocalDateTime.now(),

    @Column(name = "tipo_evento", length = 2)
    var tipoEvento: String = "B", // B = Básico, P = Premium

    @OneToMany(mappedBy = "evento", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var estadisticasDiarias: MutableList<EstadisticasEventoDiario> = mutableListOf()
) {
    fun esPremium(): Boolean = estadisticasDiarias.isNotEmpty()
}


