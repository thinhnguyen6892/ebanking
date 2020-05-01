package edu.hcmus.project.ebanking.data.repository;

import edu.hcmus.project.ebanking.data.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
