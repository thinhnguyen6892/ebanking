package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.model.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Integer> {
    List<Debt> findDebtByHolderAndDebtor(User holder, User debtor);
    List<Debt> findByStatus(DebtStatus status);
    List<Debt> findDebtByHolderOrDebtor(User holder, User debtor);
}
