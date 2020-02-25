package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findTransactionsBySourceAndTypeOrderByDateDesc(String source, TransactionType type);
    List<Transaction> findTransactionsBySourceOrderByDateDesc(String source);
}
