package tn.enicarthage.backendandroid.dto.request;
import tn.enicarthage.backendandroid.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardDTO {
    private Long id;
    private Long userId;
    private PaymentMethod paymentMethod;
    private String cardToken;
    private String last4Digits;
    private String expiryDate;
    private String cardHolderName;
}
