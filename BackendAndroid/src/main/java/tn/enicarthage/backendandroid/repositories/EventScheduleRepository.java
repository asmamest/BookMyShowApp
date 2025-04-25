package tn.enicarthage.backendandroid.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.EventSchedule;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {
    List<EventSchedule> findByEvent_Id(Long eventId);
    List<EventSchedule> findByLieu_Id(Long lieuId);
    List<EventSchedule> findByDateTimeAfter(LocalDateTime dateTime);
    List<EventSchedule> findByIsSoldOut(boolean isSoldOut);

    @Query("SELECT es FROM EventSchedule es WHERE es.dateTime BETWEEN ?1 AND ?2")
    List<EventSchedule> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
