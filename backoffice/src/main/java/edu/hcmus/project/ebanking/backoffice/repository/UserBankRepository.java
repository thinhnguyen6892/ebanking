package edu.hcmus.project.ebanking.backoffice.repository;

import edu.hcmus.project.ebanking.backoffice.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBankRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByRole(String role);
    Optional<User> findOneByEmail(String email);
}
