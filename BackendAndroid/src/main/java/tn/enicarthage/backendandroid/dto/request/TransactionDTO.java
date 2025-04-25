package tn.enicarthage.backendandroid.dto.request;

import tn.enicarthage.backendandroid.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private PaymentMethod paymentMethod;
    private LocalDateTime paidAt;
}
