/*package tn.enicarthage.backendandroid.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.Event;
import tn.enicarthage.backendandroid.models.Ticket;
import tn.enicarthage.backendandroid.enums.TicketType;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser_Id(Long userId);
    List<Ticket> findByEventSchedule_Id(Long eventScheduleId);
    List<Ticket> findByType(TicketType type);
    List<Ticket> findByIsExpired(boolean isExpired);
    List<Ticket> findByEvent(Event event);
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.eventSchedule.id = ?1")
    int countTicketsByEventScheduleId(Long eventScheduleId);

    @Query("SELECT SUM(t.nbTicketsReserved) FROM Ticket t WHERE t.eventSchedule.id = ?1")
    int sumTicketsReservedByEventScheduleId(Long eventScheduleId);
}*/
