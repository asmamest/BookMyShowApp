/*package tn.enicarthage.backendandroid.controllers;
import tn.enicarthage.backendandroid.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.backendandroid.enums.TicketType;
import tn.enicarthage.backendandroid.models.Booking;
import tn.enicarthage.backendandroid.models.Event;
import tn.enicarthage.backendandroid.models.User;
import tn.enicarthage.backendandroid.repositories.EventRepository;
import tn.enicarthage.backendandroid.repositories.UserRepository;
//import tn.enicarthage.backendandroid.services.BookingService;
import jakarta.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    public BookingController(BookingService bookingService, UserRepository userRepository,
                             EventRepository eventRepository, EmailService emailService) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            Long eventId = Long.parseLong(request.get("eventId").toString());
            boolean isStudentDiscount = Boolean.parseBoolean(request.get("isStudentDiscount").toString());
            String paymentMethod = request.get("paymentMethod").toString();

            @SuppressWarnings("unchecked")
            Map<String, Integer> ticketQuantitiesMap = (Map<String, Integer>) request.get("ticketQuantities");

            Map<TicketType, Integer> ticketQuantities = new HashMap<>();
            for (Map.Entry<String, Integer> entry : ticketQuantitiesMap.entrySet()) {
                // Convert string to TicketType enum using valueOf()
                TicketType ticketType = TicketType.valueOf(entry.getKey().toUpperCase()); // Ensure it's case-insensitive
                ticketQuantities.put(ticketType, entry.getValue());
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

            Booking booking = bookingService.createBooking(user, event, ticketQuantities, isStudentDiscount, paymentMethod);

            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/{referenceNumber}")
    public ResponseEntity<?> getBookingByReference(@PathVariable String referenceNumber) {
        try {
            Booking booking = bookingService.getBookingByReference(referenceNumber);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<Booking> bookings = bookingService.getUserBookings(user);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/resend-confirmation/{referenceNumber}")
    public ResponseEntity<?> resendConfirmationEmail(@PathVariable String referenceNumber) {
        try {
            Booking booking = bookingService.getBookingByReference(referenceNumber);

            // Réenvoyer l'email de confirmation
            try {
                emailService.sendBookingConfirmation(booking);
                return ResponseEntity.ok("Email de confirmation renvoyé avec succès");
            } catch (MessagingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur lors de l'envoi de l'email: " + e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
*/