package org.example.src.controllers

import org.example.src.dto.DailyEventStatsResponse
import org.example.src.services.EventStatsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/event-stats")
class EventStatsController(
    private val statsService: EventStatsService
) {

    @GetMapping("/{eventId}")
    fun getDailyStats(
        @PathVariable eventId: Int,
        @RequestParam organizerId: Int
    ): DailyEventStatsResponse {
        return statsService.getDailyStats(eventId, organizerId)
    }
}
