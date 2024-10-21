package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.web.CheckWebClient;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CheckWebClient checkWebClient;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void processTransaction_Success() {
        Transaction transaction = new Transaction();
        transaction.setClientId(1L);
        transaction.setAmount(BigDecimal.valueOf(500));

        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setBlocked(false);
        when(checkWebClient.check(transaction.getClientId())).thenReturn(Optional.of(checkResponse));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(result).isEqualTo(transaction);

        verify(transactionRepository).save(any(Transaction.class));
        verify(checkWebClient).check(transaction.getClientId());
    }

    @Test
    void processTransaction_Failure_BlockedClient() {
        Transaction transaction = new Transaction();
        transaction.setClientId(2L);
        transaction.setAmount(BigDecimal.valueOf(1000));

        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setBlocked(true);
        when(checkWebClient.check(transaction.getClientId())).thenReturn(Optional.of(checkResponse));

        assertThatThrownBy(() -> transactionService.processTransaction(transaction))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transaction not allowed");

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(checkWebClient).check(transaction.getClientId());
    }

    @Test
    void processTransaction_Failure_CheckResponseEmpty() {
        Transaction transaction = new Transaction();
        transaction.setClientId(3L);
        transaction.setAmount(BigDecimal.valueOf(1000));

        when(checkWebClient.check(transaction.getClientId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.processTransaction(transaction))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Transaction not allowed");

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(checkWebClient).check(transaction.getClientId());
    }
}