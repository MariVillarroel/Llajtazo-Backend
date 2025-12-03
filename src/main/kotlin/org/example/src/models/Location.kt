package org.example.src.models

import jakarta.persistence.*

@Entity
@Table(name = "lugares", schema = "llajtazo")
class Location(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,

    @Column(name = "nombre", nullable = false)
    var name: String,

    @Column(name = "direccion")
    var address: String? = null,

    @Column(name = "latitud")
    var latitude: Double? = null,

    @Column(name = "longitud")
    var longitude: Double? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Location) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "Location(id=$id, name='$name', address=$address, latitude=$latitude, longitude=$longitude)"
    }
}