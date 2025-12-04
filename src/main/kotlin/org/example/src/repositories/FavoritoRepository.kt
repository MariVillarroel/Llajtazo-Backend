package org.example.src.repositories

import org.example.src.models.Favorito
import org.example.src.models.FavoritoId
import org.springframework.data.jpa.repository.JpaRepository

interface FavoritoRepository : JpaRepository<Favorito, FavoritoId> {

    // ✔ Validar duplicados (un evento no se guarda 2 veces)
    fun existsByAsistente_IdAndEvento_Id(asistenteId: Int, eventoId: Int): Boolean

    // ✔ Listar eventos guardados en orden cronológico (por creado_en desc)
    fun findAllByAsistente_IdOrderByCreatedAtDesc(asistenteId: Int): List<Favorito>
}
