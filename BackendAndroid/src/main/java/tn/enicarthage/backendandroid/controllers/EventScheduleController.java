package tn.enicarthage.backendandroid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.backendandroid.dto.request.EventScheduleDTO;
import tn.enicarthage.backendandroid.services.EventScheduleService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/event-schedules")
@RequiredArgsConstructor
public class EventScheduleController {
    private final EventScheduleService eventScheduleService;

    @GetMapping
    public ResponseEntity<List<EventScheduleDTO>> getAllEventSchedules() {
        return ResponseEntity.ok(eventScheduleService.getAllEventSchedules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventScheduleDTO> getEventScheduleById(@PathVariable Long id) {
        return eventScheduleService.getEventScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventScheduleDTO>> getEventSchedulesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventScheduleService.getEventSchedulesByEventId(eventId));
    }

    @GetMapping("/lieu/{lieuId}")
    public ResponseEntity<List<EventScheduleDTO>> getEventSchedulesByLieuId(@PathVariable Long lieuId) {
        return ResponseEntity.ok(eventScheduleService.getEventSchedulesByLieuId(lieuId));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventScheduleDTO>> getUpcomingEventSchedules() {
        return ResponseEntity.ok(eventScheduleService.getUpcomingEventSchedules());
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<EventScheduleDTO>> getEventSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(eventScheduleService.getEventSchedulesByDateRange(start, end));
    }

    @PostMapping
    public ResponseEntity<EventScheduleDTO> createEventSchedule(@RequestBody EventScheduleDTO eventScheduleDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventScheduleService.createEventSchedule(eventScheduleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventScheduleDTO> updateEventSchedule(@PathVariable Long id, @RequestBody EventScheduleDTO eventScheduleDTO) {
        return eventScheduleService.updateEventSchedule(id, eventScheduleDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventSchedule(@PathVariable Long id) {
        if (eventScheduleService.deleteEventSchedule(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
