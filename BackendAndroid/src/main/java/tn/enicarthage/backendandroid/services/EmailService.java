/*package tn.enicarthage.backendandroid.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tn.enicarthage.backendandroid.models.Booking;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PdfService pdfService;

    public void sendBookingConfirmation(Booking booking) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(booking.getUser().getEmail());
        helper.setSubject("Confirmation de réservation - " + booking.getEventSchedule().getEvent().getTitle());

        // Préparer le contexte pour le template
        Context context = new Context(Locale.FRENCH);
        context.setVariable("booking", booking);
        context.setVariable("user", booking.getUser());
        context.setVariable("event", booking.getEventSchedule().getEvent().getTitle());
        context.setVariable("bookingDate", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(booking.getBookingDate()));

        // Générer le contenu HTML à partir du template
        String emailContent = templateEngine.process("email/booking-confirmation", context);
        helper.setText(emailContent, true);

        // Générer le PDF du billet
        ByteArrayOutputStream pdfStream = pdfService.generateTicketPdf(booking);
        helper.addAttachment("Ticket_" + booking.getReferenceNumber() + ".pdf",
                new ByteArrayResource(pdfStream.toByteArray()));

        // Envoyer l'email
        mailSender.send(message);
    }
}
*/