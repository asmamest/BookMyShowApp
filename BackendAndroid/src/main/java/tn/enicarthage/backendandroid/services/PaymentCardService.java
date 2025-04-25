package tn.enicarthage.backendandroid.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.dto.request.PaymentCardDTO;
import tn.enicarthage.backendandroid.models.PaymentCard;
import tn.enicarthage.backendandroid.models.User;
import tn.enicarthage.backendandroid.repositories.PaymentCardRepository;
import tn.enicarthage.backendandroid.repositories.UserRepository;
import tn.enicarthage.backendandroid.enums.PaymentMethod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardService {
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public List<PaymentCardDTO> getAllPaymentCards() {
        return paymentCardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PaymentCardDTO> getPaymentCardById(Long id) {
        return paymentCardRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<PaymentCardDTO> getPaymentCardsByUserId(Long userId) {
        return paymentCardRepository.findByUser_Id(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentCardDTO> getPaymentCardsByMethod(PaymentMethod paymentMethod) {
        return paymentCardRepository.findByPaymentMethod(paymentMethod).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentCardDTO createPaymentCard(PaymentCardDTO paymentCardDTO) {
        Optional<User> userOpt = userRepository.findById(paymentCardDTO.getUserId());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setUser(userOpt.get());
        paymentCard.setPaymentMethod(paymentCardDTO.getPaymentMethod());
        paymentCard.setCardToken(paymentCardDTO.getCardToken());
        paymentCard.setLast4Digits(paymentCardDTO.getLast4Digits());
        paymentCard.setExpiryDate(paymentCardDTO.getExpiryDate());
        paymentCard.setCardHolderName(paymentCardDTO.getCardHolderName());

        PaymentCard savedPaymentCard = paymentCardRepository.save(paymentCard);
        return convertToDTO(savedPaymentCard);
    }

    public Optional<PaymentCardDTO> updatePaymentCard(Long id, PaymentCardDTO paymentCardDTO) {
        if (!paymentCardRepository.existsById(id)) {
            return Optional.empty();
        }

        Optional<User> userOpt = userRepository.findById(paymentCardDTO.getUserId());

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(id);
        paymentCard.setUser(userOpt.get());
        paymentCard.setPaymentMethod(paymentCardDTO.getPaymentMethod());
        paymentCard.setCardToken(paymentCardDTO.getCardToken());
        paymentCard.setLast4Digits(paymentCardDTO.getLast4Digits());
        paymentCard.setExpiryDate(paymentCardDTO.getExpiryDate());
        paymentCard.setCardHolderName(paymentCardDTO.getCardHolderName());

        PaymentCard updatedPaymentCard = paymentCardRepository.save(paymentCard);
        return Optional.of(convertToDTO(updatedPaymentCard));
    }

    public boolean deletePaymentCard(Long id) {
        if (!paymentCardRepository.existsById(id)) {
            return false;
        }

        paymentCardRepository.deleteById(id);
        return true;
    }

    private PaymentCardDTO convertToDTO(PaymentCard paymentCard) {
        return new PaymentCardDTO(
                paymentCard.getId(),
                paymentCard.getUser().getId(),
                paymentCard.getPaymentMethod(),
                paymentCard.getCardToken(),
                paymentCard.getLast4Digits(),
                paymentCard.getExpiryDate(),
                paymentCard.getCardHolderName()
        );
    }
}
