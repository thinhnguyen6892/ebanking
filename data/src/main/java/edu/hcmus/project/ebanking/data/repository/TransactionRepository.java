package edu.hcmus.project.ebanking.data.repository;

import edu.hcmus.project.ebanking.data.model.Bank;
import edu.hcmus.project.ebanking.data.model.Transaction;
import edu.hcmus.project.ebanking.data.model.contranst.TransactionStatus;
import edu.hcmus.project.ebanking.data.model.contranst.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findTransactionsByDateBetweenAndReferenceAndStatus(ZonedDateTime startDate, ZonedDateTime endDate, Bank bank, TransactionStatus status, Pageable pageable);
    Page<Transaction> findTransactionsByDateBetweenAndStatusAndReferenceNotNull(ZonedDateTime startDate, ZonedDateTime endDate, TransactionStatus status, Pageable pageable);
    Page<Transaction> findTransactionsByDateBetweenAndReferenceAndTypeAndStatus(ZonedDateTime startDate, ZonedDateTime endDate, Bank bank, TransactionType type, TransactionStatus status, Pageable pageable);
    Page<Transaction> findTransactionsByDateBetweenAndTypeAndStatusAndReferenceNotNull(ZonedDateTime startDate, ZonedDateTime endDate, TransactionType type, TransactionStatus status, Pageable pageable);
    Page<Transaction> findTransactionsBySourceAndTypeAndStatus(String source, TransactionType type, TransactionStatus status, Pageable pageable);
    Page<Transaction> findTransactionsBySourceAndStatusOrderByDateDesc(String source, TransactionStatus status, Pageable pageable);
}
