package tn.enicarthage.backendandroid.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.Event;
import tn.enicarthage.backendandroid.enums.CategoryEvent;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(CategoryEvent category);
    List<Event> findByTitleContainingIgnoreCase(String title);
}
