package edu.hcmus.project.ebanking.backoffice.resource.user;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserResourceRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setRole(dto.getRole());
        newUser.setStatus(dto.getStatus());
        newUser.setEmail(dto.getEmail());
        newUser = userRepository.save(newUser);
        dto.setPassword("");
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

}
