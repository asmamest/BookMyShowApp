package tn.enicarthage.backendandroid.models;

import tn.enicarthage.backendandroid.enums.TicketType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ref;

    @ManyToOne
    @JoinColumn(name = "event_schedule_id")
    private EventSchedule eventSchedule;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToOne
    @JoinColumn(name = "transaction_id", unique = true)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    private int nbTicketsReserved;

    private double price;

    private String qrCode;

    private boolean isExpired;

}
