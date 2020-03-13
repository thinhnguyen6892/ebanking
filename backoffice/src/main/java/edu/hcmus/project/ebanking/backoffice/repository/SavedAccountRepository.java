package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.SavedAccount;
import edu.hcmus.project.ebanking.backoffice.model.User;
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
}
