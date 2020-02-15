package edu.hcmus.project.ebanking.backoffice.resource.account;

import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class AccountResourceRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/accounts/user/{userId}")
    public List<AccountDto> retrieveAllUserAccounts(@PathVariable Long userId) {
        return userService.findUserAccount(userId);
    }

    //Todo Hidden account id
    @GetMapping("/accounts/{accountId}")
    public AccountDto retrieveUserAccount(@PathVariable String accountId) {
        return userService.findAccount(accountId);
    }

    @PutMapping("/accounts")
    public ResponseEntity<AccountDto> updateAccount(@Valid @RequestBody AccountDto dto) {
        String accountId = userService.updateAccount(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accountId).toUri();
        return ResponseEntity.status(HttpStatus.ACCEPTED).location(location).build();
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto dto) {
        String accountId = userService.createAccount(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accountId).toUri();
        return ResponseEntity.created(location).build();
    }

}
