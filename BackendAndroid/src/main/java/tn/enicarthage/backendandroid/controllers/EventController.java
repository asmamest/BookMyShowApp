package tn.enicarthage.backendandroid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.backendandroid.dto.request.EventDTO;
import tn.enicarthage.backendandroid.services.EventService;
import tn.enicarthage.backendandroid.enums.CategoryEvent;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventDTO>> getEventsByCategory(@PathVariable CategoryEvent category) {
        return ResponseEntity.ok(eventService.getEventsByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEventsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(eventService.searchEventsByTitle(title));
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        return eventService.updateEvent(id, eventDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
