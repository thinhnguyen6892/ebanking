package edu.hcmus.project.ebanking.data.repository;

import edu.hcmus.project.ebanking.data.model.Role;
import edu.hcmus.project.ebanking.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndStatusIsTrue(String username);
    User findByUsername(String userName);
    List<User> findByRole(Role role);
    Optional<User> findOneByEmail(String email);
    User findByEmail(String email);
}
