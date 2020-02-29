package edu.hcmus.project.ebanking.ws.repository;

import edu.hcmus.project.ebanking.ws.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, String> {
}
