/*package tn.enicarthage.backendandroid.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.Booking;
import tn.enicarthage.backendandroid.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    Optional<Booking> findByReferenceNumber(String referenceNumber);
}
*/