package tn.enicarthage.backendandroid.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Lieu lieu;

    private LocalDateTime dateTime;

    @Setter
    private boolean isSoldOut;

    public void setIsSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public boolean isIsSoldOut() {
        return isSoldOut;
    }
}
