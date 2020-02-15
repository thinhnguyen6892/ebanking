package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Role;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.RoleRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean createUser(UserDto dto) {
        Optional<Role> roleOp = roleRepository.findById("USER");
        if(roleOp.isPresent()) {
            User newUser = new User();
            newUser.setUsername(dto.getUsername());
            newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            newUser.setRole(roleOp.get());
            newUser.setStatus(dto.getStatus());
            newUser.setEmail(dto.getEmail());
            newUser = userRepository.save(newUser);
            Account account = new Account();
            account.setType("Payment");
            account.setOwner(newUser);
            account.setStatus(true);
            account.setBalance(0);
            return true;
        }
        return false;
    }
}
