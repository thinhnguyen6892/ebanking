package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByOwnerAndAccountId(User owner, String id);
    List<Account> findAccountsByOwner(User owner);
    List<Account> findAccountsByOwnerAndType(User owner, String type);
}
