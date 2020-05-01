package edu.hcmus.project.ebanking.data.repository;

import edu.hcmus.project.ebanking.data.model.SavedAccount;
import edu.hcmus.project.ebanking.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedAccountRepository extends JpaRepository<SavedAccount, Integer> {
    Optional<SavedAccount> findByAccountId(String accountId);
    List<SavedAccount> findByOwner(User owner);
    Optional<SavedAccount> findByIdAndOwner(Integer id, User owner);
    List<SavedAccount> findByOwnerAndNameSuggestionStartingWith(User owner, String name);
    List<SavedAccount> findByAccountIdStartingWith(String accountid);
}
