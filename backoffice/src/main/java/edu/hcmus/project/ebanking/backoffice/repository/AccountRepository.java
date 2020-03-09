package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findAccountsByOwner(User owner);
    List<Account> findAccountsByOwnerAndType(User owner, String type);
    Account findAccountByAccountId(String accountId);
}
