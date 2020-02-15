package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, String> {
    Bank findByBankName(String bankName);
    List<Bank> findByKey(String key);
    Bank findById(Long id);
}
