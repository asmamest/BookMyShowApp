package tn.enicarthage.backendandroid.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.dto.request.TransactionDTO;
import tn.enicarthage.backendandroid.models.Transaction;
import tn.enicarthage.backendandroid.repositories.TransactionRepository;
import tn.enicarthage.backendandroid.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TransactionDTO> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<TransactionDTO> getTransactionsByPaymentMethod(PaymentMethod paymentMethod) {
        return transactionRepository.findByPaymentMethod(paymentMethod).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByPaidAtBetween(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setPaymentMethod(transactionDTO.getPaymentMethod());
        transaction.setPaidAt(transactionDTO.getPaidAt() != null ? transactionDTO.getPaidAt() : LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    public Optional<TransactionDTO> updateTransaction(Long id, TransactionDTO transactionDTO) {
        if (!transactionRepository.existsById(id)) {
            return Optional.empty();
        }

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setPaymentMethod(transactionDTO.getPaymentMethod());
        transaction.setPaidAt(transactionDTO.getPaidAt());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return Optional.of(convertToDTO(updatedTransaction));
    }

    public boolean deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            return false;
        }

        transactionRepository.deleteById(id);
        return true;
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getPaymentMethod(),
                transaction.getPaidAt()
        );
    }
}
