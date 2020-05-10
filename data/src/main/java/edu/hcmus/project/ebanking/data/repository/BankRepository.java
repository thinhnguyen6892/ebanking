package edu.hcmus.project.ebanking.data.repository;

import edu.hcmus.project.ebanking.data.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {
}
