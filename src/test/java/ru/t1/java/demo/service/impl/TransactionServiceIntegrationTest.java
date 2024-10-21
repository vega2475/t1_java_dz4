package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.web.CheckWebClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
class TransactionServiceIntegrationTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private CheckWebClient checkWebClient;

    @Test
    void processTransactionTest_allowedTransaction() {
        Transaction transaction = new Transaction();
        transaction.setClientId(1L);
        transaction.setAmount(BigDecimal.valueOf(500));

        when(transactionRepository.save(ArgumentMatchers.any(Transaction.class)))
                .thenReturn(transaction);

        Transaction processedTransaction = transactionService.processTransaction(transaction);

        assertThat(processedTransaction).isNotNull();
        assertThat(processedTransaction.getClientId()).isEqualTo(1L);
        assertThat(processedTransaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }
}
