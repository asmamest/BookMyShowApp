package tn.enicarthage.backendandroid.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.enicarthage.backendandroid.models.PaymentCard;
import tn.enicarthage.backendandroid.enums.PaymentMethod;
import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    List<PaymentCard> findByUser_Id(Long userId);
    List<PaymentCard> findByPaymentMethod(PaymentMethod paymentMethod);
}
