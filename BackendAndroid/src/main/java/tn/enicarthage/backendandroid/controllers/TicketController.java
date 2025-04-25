package tn.enicarthage.backendandroid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.backendandroid.dto.request.TicketDTO;
import tn.enicarthage.backendandroid.services.TicketService;
import tn.enicarthage.backendandroid.enums.TicketType;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketDTO>> getTicketsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }

    @GetMapping("/event-schedule/{eventScheduleId}")
    public ResponseEntity<List<TicketDTO>> getTicketsByEventScheduleId(@PathVariable Long eventScheduleId) {
        return ResponseEntity.ok(ticketService.getTicketsByEventScheduleId(eventScheduleId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TicketDTO>> getTicketsByType(@PathVariable TicketType type) {
        return ResponseEntity.ok(ticketService.getTicketsByType(type));
    }

    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@RequestBody TicketDTO ticketDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(ticketDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable Long id, @RequestBody TicketDTO ticketDTO) {
        return ticketService.updateTicket(id, ticketDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (ticketService.deleteTicket(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
