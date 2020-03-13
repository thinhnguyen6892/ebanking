package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Integer> {
    List<Debt> findByStatus(DebtStatus status);
    List<Debt> findDebtByHolderOrDebtor(User holder, User debtor);
    List<Debt> findNewDebtByDebtorAndStatus(User debtor, DebtStatus status);
}
