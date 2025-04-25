package tn.enicarthage.backendandroid.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enicarthage.backendandroid.dto.request.PaymentCardDTO;
import tn.enicarthage.backendandroid.services.PaymentCardService;
import tn.enicarthage.backendandroid.enums.PaymentMethod;

import java.util.List;

@RestController
@RequestMapping("/api/payment-cards")
@RequiredArgsConstructor
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    @GetMapping
    public ResponseEntity<List<PaymentCardDTO>> getAllPaymentCards() {
        return ResponseEntity.ok(paymentCardService.getAllPaymentCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> getPaymentCardById(@PathVariable Long id) {
        return paymentCardService.getPaymentCardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardDTO>> getPaymentCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentCardService.getPaymentCardsByUserId(userId));
    }

    @GetMapping("/method/{paymentMethod}")
    public ResponseEntity<List<PaymentCardDTO>> getPaymentCardsByMethod(@PathVariable PaymentMethod paymentMethod) {
        return ResponseEntity.ok(paymentCardService.getPaymentCardsByMethod(paymentMethod));
    }

    @PostMapping
    public ResponseEntity<PaymentCardDTO> createPaymentCard(@RequestBody PaymentCardDTO paymentCardDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.createPaymentCard(paymentCardDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDTO> updatePaymentCard(@PathVariable Long id, @RequestBody PaymentCardDTO paymentCardDTO) {
        return paymentCardService.updatePaymentCard(id, paymentCardDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentCard(@PathVariable Long id) {
        if (paymentCardService.deletePaymentCard(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
