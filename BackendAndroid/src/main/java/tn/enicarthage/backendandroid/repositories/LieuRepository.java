package tn.enicarthage.backendandroid.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.Lieu;
import java.util.List;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
    List<Lieu> findByNameContainingIgnoreCase(String name);
    List<Lieu> findByCapacityGreaterThanEqual(int capacity);
}
