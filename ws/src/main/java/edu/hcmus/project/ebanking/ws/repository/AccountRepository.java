package edu.hcmus.project.ebanking.ws.repository;

import edu.hcmus.project.ebanking.ws.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

}
