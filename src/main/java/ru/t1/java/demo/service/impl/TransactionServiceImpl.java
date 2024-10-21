package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.web.CheckWebClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CheckWebClient checkWebClient;
    private final TransactionRepository transactionRepository;

    /**
     * Метод для обработки транзакции.
     * Проверяет разрешение через заглушку wiremock прежде чем проводить транзакцию.
     */
    public Transaction processTransaction(Transaction transaction) {
        log.info("Проверка разрешения на транзакцию для клиента с ID {}", transaction.getClientId());
        Optional<CheckResponse> response = checkWebClient.check(transaction.getClientId());

        if (response.isPresent() && !response.get().getBlocked()) {
            log.info("Транзакция разрешена для клиента с id {}. Проводим транзакцию.", transaction.getClientId());

            return executeTransaction(transaction);
        } else {
            log.warn("Транзакция отклонена для клиента с id {}", transaction.getClientId());
            throw new RuntimeException("Transaction not allowed");
        }
    }

    /**
     *  Логика выполнения транзакции.
     */
    private Transaction executeTransaction(Transaction transaction) {
        log.info("Выполнение транзакции на сумму {} для клиента {}", transaction.getAmount(), transaction.getClientId());
        return transactionRepository.save(transaction);
    }
}
