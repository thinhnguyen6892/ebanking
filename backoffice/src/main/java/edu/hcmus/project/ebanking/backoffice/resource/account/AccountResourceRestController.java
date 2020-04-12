package edu.hcmus.project.ebanking.backoffice.resource.account;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.AccountDto;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.CreateAccount;
import edu.hcmus.project.ebanking.backoffice.resource.account.dto.DepositAccount;
import edu.hcmus.project.ebanking.backoffice.service.AccountService;
import edu.hcmus.project.ebanking.backoffice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Api(value="Account Management Resource")
@RestController
@RequestMapping("/accounts")
public class AccountResourceRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "[Employee] Find account by accountId")
    @GetMapping
    public AccountDto findAccountByAccountId(@RequestParam String accountId, @RequestParam String bankId) {
        return accountService.findAccountByAccountId(accountId);
    }

    @ApiOperation(value = "1.2 [USER] View a list of available account of current log-on user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public List<AccountDto> retrieveAllUserAccounts() {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return accountService.findUserAccounts(userDetails.getId(), false);
    }

    @ApiOperation(value = "[Employee] View a list of available user account on the system by user id. ", response = List.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/user/{id}")
    public List<AccountDto> retrieveAllUserAccounts(@PathVariable long id) {
        return accountService.findUserAccounts(id, true);
    }

    @ApiOperation(value = "[Employee] Deposit into account")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/deposit")
    public ResponseEntity<Void> depositAccount(@Valid @RequestBody DepositAccount dto) {
        accountService.depositAccount(dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccount dto) {
        String accountId = accountService.createAccount(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accountId).toUri();
        return ResponseEntity.created(location).build();
    }


    @ApiOperation(value = "[Employee] Find accounts by userName")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/username/{userName}")
    public List<AccountDto> findAccountsByUserName(@PathVariable String userName) {
        return accountService.findAccountByUserName(userName);
    }

}
