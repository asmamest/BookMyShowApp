package tn.enicarthage.backendandroid.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.Transaction;
import tn.enicarthage.backendandroid.enums.PaymentMethod;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Transaction> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}
