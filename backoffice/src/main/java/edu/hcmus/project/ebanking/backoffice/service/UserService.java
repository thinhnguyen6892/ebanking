package edu.hcmus.project.ebanking.backoffice.service;

import edu.hcmus.project.ebanking.backoffice.model.Account;
import edu.hcmus.project.ebanking.backoffice.model.Role;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.AccountRepository;
import edu.hcmus.project.ebanking.backoffice.repository.RoleRepository;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setUsername(user.getUsername());
            dto.setStatus(user.getStatus());
            dto.setRole(user.getRole().getName());
            dto.setEmail(user.getEmail());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
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
            account.setOwner(newUser);
            accountRepository.save(account);
            return true;
        }
        return false;
    }
}
