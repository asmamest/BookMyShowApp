/*package tn.enicarthage.backendandroid.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicarthage.backendandroid.enums.TicketType;
import tn.enicarthage.backendandroid.models.*;
import tn.enicarthage.backendandroid.repositories.BookingDetailRepository;
import tn.enicarthage.backendandroid.repositories.BookingRepository;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Transactional
    public Booking createBooking(User user, Event event, Map<TicketType, Integer> ticketQuantities,
                                 boolean isStudentDiscount, String paymentMethod) {

        // Créer la réservation
        Booking booking = new Booking();
        EventSchedule eventSchedule = new EventSchedule();
        booking.setUser(user);
        booking.setEventSchedule(eventSchedule);
        booking.setBookingDate(new Date());
        booking.setReferenceNumber(generateReferenceNumber());
        booking.setIsStudentDiscount(isStudentDiscount);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus("CONFIRMED");

        // Calculer le montant total
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BookingDetail> details = new ArrayList<>();

        for (Map.Entry<TicketType, Integer> entry : ticketQuantities.entrySet()) {
            TicketType type = entry.getKey();
            int quantity = entry.getValue();

            BigDecimal typeTotal = type.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(typeTotal);

            // Créer le détail de réservation
            BookingDetail detail = new BookingDetail();
            detail.setBooking(booking);
            detail.setTicketCategory(type);
            detail.setQuantity(quantity);
            details.add(detail);
        }

        // Appliquer la remise étudiant si nécessaire
        if (isStudentDiscount) {
            BigDecimal discount = totalAmount.multiply(BigDecimal.valueOf(0.10));
            totalAmount = totalAmount.subtract(discount);
        }

        booking.setTotalAmount(totalAmount);

        // Sauvegarder la réservation
        Booking savedBooking = bookingRepository.save(booking);

        // Sauvegarder les détails
        for (BookingDetail detail : details) {
            detail.setBooking(savedBooking);
            bookingDetailRepository.save(detail);
        }

        // Envoyer l'email de confirmation avec gestion des exceptions
        try {
            emailService.sendBookingConfirmation(savedBooking);
            logger.info("Email de confirmation envoyé avec succès pour la réservation : {}", savedBooking.getReferenceNumber());
        } catch (Exception e) {  // Capture toutes les exceptions
            logger.error("Erreur lors de l'envoi de l'email de confirmation pour la réservation : {}", savedBooking.getReferenceNumber(), e);
        }


        return savedBooking;
    }

    public Booking getBookingByReference(String referenceNumber) {
        return bookingRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
    }

    public List<Booking> getUserBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    private String generateReferenceNumber() {
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);
        return String.valueOf(randomNumber);
    }
}
*/