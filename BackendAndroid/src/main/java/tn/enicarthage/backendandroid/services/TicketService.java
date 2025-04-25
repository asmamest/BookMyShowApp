package tn.enicarthage.backendandroid.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.dto.request.TicketDTO;
import tn.enicarthage.backendandroid.models.EventSchedule;
import tn.enicarthage.backendandroid.models.Ticket;
import tn.enicarthage.backendandroid.models.Transaction;
import tn.enicarthage.backendandroid.models.User;
import tn.enicarthage.backendandroid.repositories.EventScheduleRepository;
import tn.enicarthage.backendandroid.repositories.TicketRepository;
import tn.enicarthage.backendandroid.repositories.TransactionRepository;
import tn.enicarthage.backendandroid.repositories.UserRepository;
import tn.enicarthage.backendandroid.enums.TicketType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TicketDTO> getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<TicketDTO> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUser_Id(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByEventScheduleId(Long eventScheduleId) {
        return ticketRepository.findByEventSchedule_Id(eventScheduleId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByType(TicketType type) {
        return ticketRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Optional<EventSchedule> eventScheduleOpt = eventScheduleRepository.findById(ticketDTO.getEventScheduleId());
        Optional<User> userOpt = userRepository.findById(ticketDTO.getUserId());
        Optional<Transaction> transactionOpt = ticketDTO.getTransactionId() != null ?
                transactionRepository.findById(ticketDTO.getTransactionId()) : Optional.empty();

        if (eventScheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("EventSchedule not found");
        }

        EventSchedule eventSchedule = eventScheduleOpt.get();

        // Vérifier si l'événement est complet
        if (eventSchedule.isIsSoldOut()) {
            throw new IllegalStateException("Event is sold out");
        }

        // Vérifier la capacité disponible
        int totalReservedTickets = ticketRepository.sumTicketsReservedByEventScheduleId(eventSchedule.getId());
        int remainingCapacity = eventSchedule.getLieu().getCapacity() - totalReservedTickets;

        if (ticketDTO.getNbTicketsReserved() > remainingCapacity) {
            throw new IllegalStateException("Not enough capacity available");
        }

        Ticket ticket = new Ticket();
        ticket.setEventSchedule(eventSchedule);

        if (userOpt.isPresent()) {
            ticket.setUser(userOpt.get());
        }

        if (transactionOpt.isPresent()) {
            ticket.setTransaction(transactionOpt.get());
        }

        ticket.setType(ticketDTO.getType());
        ticket.setNbTicketsReserved(ticketDTO.getNbTicketsReserved());
        ticket.setPrice(ticketDTO.getPrice());

        // Générer un QR code unique
        ticket.setQrCode(UUID.randomUUID().toString());
        ticket.setExpired(false);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Mettre à jour le statut "sold out" si nécessaire
        if (totalReservedTickets + ticketDTO.getNbTicketsReserved() >= eventSchedule.getLieu().getCapacity()) {
            eventSchedule.setIsSoldOut(true);
            eventScheduleRepository.save(eventSchedule);
        }

        return convertToDTO(savedTicket);
    }

    public Optional<TicketDTO> updateTicket(Long id, TicketDTO ticketDTO) {
        if (!ticketRepository.existsById(id)) {
            return Optional.empty();
        }

        Optional<EventSchedule> eventScheduleOpt = eventScheduleRepository.findById(ticketDTO.getEventScheduleId());
        Optional<User> userOpt = userRepository.findById(ticketDTO.getUserId());
        Optional<Transaction> transactionOpt = ticketDTO.getTransactionId() != null ?
                transactionRepository.findById(ticketDTO.getTransactionId()) : Optional.empty();

        if (eventScheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("EventSchedule not found");
        }

        Ticket ticket = new Ticket();
        ticket.setRef(id);
        ticket.setEventSchedule(eventScheduleOpt.get());

        if (userOpt.isPresent()) {
            ticket.setUser(userOpt.get());
        }

        if (transactionOpt.isPresent()) {
            ticket.setTransaction(transactionOpt.get());
        }

        ticket.setType(ticketDTO.getType());
        ticket.setNbTicketsReserved(ticketDTO.getNbTicketsReserved());
        ticket.setPrice(ticketDTO.getPrice());
        ticket.setQrCode(ticketDTO.getQrCode());
        ticket.setExpired(ticketDTO.isExpired());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return Optional.of(convertToDTO(updatedTicket));
    }

    public boolean deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            return false;
        }

        ticketRepository.deleteById(id);
        return true;
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        EventSchedule eventSchedule = ticket.getEventSchedule();
        // Calculer l'heure de check-in (3 heures avant l'événement)
        LocalDateTime checkinTime = eventSchedule.getDateTime().minusHours(3);

        return new TicketDTO(
                ticket.getRef(),
                eventSchedule.getId(),
                ticket.getUser() != null ? ticket.getUser().getId() : null,
                ticket.getTransaction() != null ? ticket.getTransaction().getId() : null,
                ticket.getType(),
                ticket.getNbTicketsReserved(),
                ticket.getPrice(),
                ticket.getQrCode(),
                ticket.isExpired(),
                eventSchedule.getEvent().getTitle(),
                eventSchedule.getDateTime(),
                checkinTime,
                eventSchedule.getLieu().getName()
        );
    }
}
