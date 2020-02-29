package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.model.TransactionStatus;
import edu.hcmus.project.ebanking.backoffice.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findTransactionsByDateBetweenAndReferenceAndStatusNotOrderByDateDesc(ZonedDateTime startDate, ZonedDateTime endDate, Bank bank, TransactionStatus status);
    List<Transaction> findTransactionsByDateBetweenAndStatusNotOrderByDateDesc(ZonedDateTime startDate, ZonedDateTime endDate, TransactionStatus status);
    List<Transaction> findTransactionsBySourceAndTypeAndStatusNotOrderByDateDesc(String source, TransactionType type, TransactionStatus status);
    List<Transaction> findTransactionsBySourceAndStatusNotOrderByDateDesc(String source, TransactionStatus status);
}
