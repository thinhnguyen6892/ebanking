package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionStatus;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findTransactionsByDateBetweenAndReferenceAndStatusOrderByDateDesc(ZonedDateTime startDate, ZonedDateTime endDate, Bank bank, TransactionStatus status);
    List<Transaction> findTransactionsByDateBetweenAndStatusOrderByDateDesc(ZonedDateTime startDate, ZonedDateTime endDate, TransactionStatus status);
    List<Transaction> findTransactionsBySourceAndTypeAndStatusOrderByDateDesc(String source, TransactionType type, TransactionStatus status);
    List<Transaction> findTransactionsBySourceAndStatusOrderByDateDesc(String source, TransactionStatus status);
}
