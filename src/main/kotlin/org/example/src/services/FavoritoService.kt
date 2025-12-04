package org.example.src.services

import org.example.src.models.Favorito
import org.example.src.models.Evento
import org.example.src.repositories.AsistenteRepository
import org.example.src.repositories.EventoRepository
import org.example.src.repositories.FavoritoRepository
import org.springframework.stereotype.Service

@Service
class FavoritoService(
    private val favoritoRepository: FavoritoRepository,
    private val asistenteRepository: AsistenteRepository,
    private val eventoRepository: EventoRepository
) {

    // 1) Guardar evento como favorito
    fun guardarFavorito(asistenteId: Int, eventoId: Int): Favorito {

        // 1.1 Validación de duplicados
        if (favoritoRepository.existsByAsistente_IdAndEvento_Id(asistenteId, eventoId)) {
            throw IllegalArgumentException("El evento ya está guardado como favorito")
        }

        // 1.2 Buscar asistente y evento
        val asistente = asistenteRepository.findById(asistenteId)
            .orElseThrow { IllegalArgumentException("No existe asistente con id=$asistenteId") }

        val evento = eventoRepository.findById(eventoId)
            .orElseThrow { IllegalArgumentException("No existe evento con id=$eventoId") }

        // 1.3 Crear y guardar favorito
        val favorito = Favorito(
            asistente = asistente,
            evento = evento
        )

        return favoritoRepository.save(favorito)
    }

    // 2) Listar favoritos del usuario en orden cronológico
    fun listarFavoritos(asistenteId: Int): List<Favorito> {
        return favoritoRepository.findAllByAsistente_IdOrderByCreatedAtDesc(asistenteId)
    }

    // 3) Integración con calendario: devolver solo los eventos
    fun listarEventosFavoritosParaCalendario(asistenteId: Int): List<Evento> {
        val favoritos = listarFavoritos(asistenteId)
        return favoritos.map { it.evento }
    }
}
