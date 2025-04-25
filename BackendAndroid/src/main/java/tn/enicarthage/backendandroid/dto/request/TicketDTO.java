package tn.enicarthage.backendandroid.dto.request;

import tn.enicarthage.backendandroid.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long ref;
    private Long eventScheduleId;
    private Long userId;
    private Long transactionId;
    private TicketType type;
    private int nbTicketsReserved;
    private double price;
    private String qrCode;
    private boolean isExpired;

    // Informations supplémentaires pour l'affichage
    private String eventTitle;
    private LocalDateTime eventDateTime;
    private LocalDateTime checkinTime;
    private String lieuName;
}
