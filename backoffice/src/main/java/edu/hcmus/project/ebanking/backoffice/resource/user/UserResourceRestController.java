package edu.hcmus.project.ebanking.backoffice.resource.user;

import edu.hcmus.project.ebanking.backoffice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
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

}
