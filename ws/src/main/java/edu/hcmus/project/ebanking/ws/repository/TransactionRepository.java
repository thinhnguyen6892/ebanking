package edu.hcmus.project.ebanking.ws.repository;

import edu.hcmus.project.ebanking.ws.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
