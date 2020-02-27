package edu.hcmus.project.ebanking.backoffice.resource.user;

import edu.hcmus.project.ebanking.backoffice.resource.exception.EntityNotExistException;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
public class UserResourceRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto, @PathVariable long id){
        boolean result = userService.updateUser(dto, id);
        dto.setPassword("");
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/recover/{email:.+}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> recoverPassword(@NotNull @PathVariable String email,
                                          @RequestParam(value = "token", required = false) String token,
                                          @RequestParam(value = "password", required = false) String password,
                                          HttpServletRequest request) {
        try {
            return new ResponseEntity(userService.recoverPassword(email, token, password, request), HttpStatus.OK);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<UserDto> changePassword(@RequestBody UserDto dto, @PathVariable String username){
        User cUser = userRepository.findByUsername(username);
        cUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        cUser = userRepository.save(cUser);
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @GetMapping("/employee")
    public List<UserDto> GetAllEmployee(){
        return userService.findAllEmployeeRole();
    }

    @GetMapping("/employee/{id}")
    public UserDto GetEmployeeById(@PathVariable long id){
        return userService.findEmployeeById(id);
    }

    @PostMapping("/employee/create")
    public ResponseEntity<UserDto> createEmployee(@Valid @RequestBody UserDto dto){
        boolean result = userService.createEmployee(dto);
        dto.setPassword("");
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @PutMapping("/employee/update/{id}")
    public ResponseEntity<UserDto> updateEmployee(@RequestBody UserDto dto, @PathVariable long id){
        boolean result = userService.updateEmployee(dto, id);
        dto.setPassword("");
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/employee/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable long id) {
        boolean result = userService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
