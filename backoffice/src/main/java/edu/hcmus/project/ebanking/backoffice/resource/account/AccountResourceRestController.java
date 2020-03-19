package edu.hcmus.project.ebanking.backoffice.resource.account;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Api(value="Account Management Resource")
@RestController
public class AccountResourceRestController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "View a list of available account of current log-on user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/accounts/user")
    public List<AccountDto> retrieveAllUserAccounts() {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userService.findUserAccount(userDetails.getId());
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
