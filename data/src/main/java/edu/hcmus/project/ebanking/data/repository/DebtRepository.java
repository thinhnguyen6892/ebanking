package edu.hcmus.project.ebanking.data.repository;

import edu.hcmus.project.ebanking.data.model.Account;
import edu.hcmus.project.ebanking.data.model.Debt;
import edu.hcmus.project.ebanking.data.model.Transaction;
import edu.hcmus.project.ebanking.data.model.User;
import edu.hcmus.project.ebanking.data.model.contranst.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Integer> {
    List<Debt> findDebtByHolderOrDebtor(User holder, Account debtor);
    List<Debt> findNewDebtByDebtorAndStatus(Account debtor_acc, DebtStatus status);
    List<Debt> findDebtByHolder(User holder);
    List<Debt> findDebtByDebtor(Account debtor);
    Optional<Debt> findDebtByPaymentRef(Transaction transaction);
    Optional<Debt> findDebtByIdAndStatus(Integer id, DebtStatus status);
}
