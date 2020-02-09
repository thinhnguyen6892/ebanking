package edu.hcmus.project.ebanking.backoffice.resource.user;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/users/{id}")
    public User getByid(@PathVariable long id){return userRepository.findById(id);}

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

    @PutMapping("/users/update/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto, @PathVariable long id){
        User upUser = userRepository.findById(id);
        upUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        upUser.setRole(dto.getRole());
        upUser.setStatus(dto.getStatus());
        upUser.setEmail(dto.getEmail());
        upUser = userRepository.save(upUser);
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
