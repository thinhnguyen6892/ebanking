package edu.hcmus.project.ebanking.backoffice.resource.user;

import edu.hcmus.project.ebanking.backoffice.resource.user.dto.ChangePasswordDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.CreateUserDto;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;
import edu.hcmus.project.ebanking.backoffice.security.jwt.JwtTokenUtil;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResourceRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "[Administrator] View a list of available users on the system. ", response = List.class)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserDto> retrieveAllUsers() {
        return userService.findAllUsers(null, true);
    }

    @ApiOperation(value = "[Administrator] View a list of available employee on the system. ", response = List.class)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employee")
    public List<UserDto> retrieveAllStaff() {
        return userService.findAllStaffs();
    }

    @ApiOperation(value = "[Administrator - Employee] Create new customer. ", response = UserDto.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/create")
    public ResponseEntity<UserDto> createCustomer(@Valid @RequestBody CreateUserDto dto) {
        UserDto newDto = userService.createCustomer(dto);
        return new ResponseEntity<UserDto>(newDto, HttpStatus.OK);
    }

    @ApiOperation(value = "[Administrator - Employee] Update customer information. ", response = UserDto.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateCustomer(@RequestBody UserDto dto, @PathVariable long id){
        dto = userService.updateCustomer(dto, id);
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "[Administrator] Delete an user. ")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "[Employee] View a list of available user on the system. ", response = List.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/customers")
    public List<UserDto> retrieveAllCustomer() {
        return userService.findAllCustomer();
    }

    @ApiOperation(value = "[Administrator] Create new employee on the system. ", response = UserDto.class)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/employee/create")
    public ResponseEntity<UserDto> createEmployee(@Valid @RequestBody CreateUserDto dto){
        UserDto newDto = userService.createEmployee(dto);
        return new ResponseEntity<UserDto>(newDto, HttpStatus.OK);
    }

    @ApiOperation(value = "[Administrator] Update an employee on the system. ", response = UserDto.class)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/employee/{id}")
    public ResponseEntity<UserDto> updateEmployee(@RequestBody UserDto dto, @PathVariable long id){
        dto = userService.updateEmployee(dto, id);
        return new ResponseEntity<UserDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.7 [User] Change password", response = String.class)
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/password")
    public ResponseEntity<String> changePassword(@Valid ChangePasswordDto dto) {
        return new ResponseEntity(userService.changePassword(JwtTokenUtil.getLoggedUser(), dto.getOldPassword(), dto.getNewPassword()), HttpStatus.OK);
    }

    @ApiOperation(value = "Recover password via email", response = String.class)
    @RequestMapping(value = "/recover/{email:.+}",
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

    @ApiOperation(value = "[Employee] Get user by userName")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> findUserByUserName(@PathVariable String id){
        UserDto user = userService.findUserByUserName(id);
        return new ResponseEntity<UserDto>(user, HttpStatus.OK);
    }
}
