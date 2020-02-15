package edu.hcmus.project.ebanking.backoffice.resource.user;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class UserResourceRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserDto> retrieveAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/users/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto dto) {
        boolean result = userService.createUser(dto);
        dto.setPassword("");
        return new ResponseEntity<UserDto>(dto, result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
