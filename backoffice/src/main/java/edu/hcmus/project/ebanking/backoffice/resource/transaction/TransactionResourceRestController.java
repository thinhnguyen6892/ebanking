package edu.hcmus.project.ebanking.backoffice.resource.transaction;

import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import edu.hcmus.project.ebanking.backoffice.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TransactionResourceRestController {

    @Autowired
    private TransactionService tranferService;

    @ApiOperation(value = "[Administrator] Retrieve all transaction including filtering by bank id.", response = List.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/transactions")
    public List<TransactionDto> retrieveAllTransaction(@RequestBody TransactionRequestDto request) {
        if(request.getStartDate() == null || request.getEndDate() == null) {
            throw new InvalidTransactionException("Invalid parameters");
        }
        return tranferService.findAllTransaction(request);
    }

    @ApiOperation(value = "[Employee] View account transaction. Filter by TransactionType [DEPOSIT, WITHDRAW, TRANSFER, PAYMENT]", response = List.class)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @PostMapping("/transactions/account")
    public List<TransactionDto> retrieveUserAllTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        return tranferService.findAllAccountTransaction(dto.getAccountId(), dto.getType());
    }

    @ApiOperation(value = "[USER] Request a new transaction", response = TransactionDto.class)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transactions/request")
    public TransactionDto requestTransaction(@Valid @RequestBody TransactionDto dto) {
        TransactionDto opt = tranferService.requestTransaction(dto);
        return opt;
    }

    @ApiOperation(value = "[USER] Confirm to complete the transaction")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transactions/pay")
    public ResponseEntity<Void> pay(@RequestBody TransactionDto dto) {
        if(dto.getId() == null || dto.getId() == "" || dto.getOtpCode() == null || dto.getOtpCode() == "") {
            return ResponseEntity.badRequest().build();
        }
        tranferService.pay(dto);
        return ResponseEntity.ok().build();
    }

}
