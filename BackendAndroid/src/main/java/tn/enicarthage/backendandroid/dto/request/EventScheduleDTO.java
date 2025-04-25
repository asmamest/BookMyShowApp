package tn.enicarthage.backendandroid.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleDTO {
    private Long id;
    private Long eventId;
    private Long lieuId;
    private LocalDateTime dateTime;
    private LocalDateTime checkinTime;
    private boolean isSoldOut;
    private String eventTitle;
    private String lieuName;
}
