package edu.hcmus.project.ebanking.backoffice.resource.transaction;

import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TransferResourceRestController {

    @Autowired
    private TransactionService tranferService;


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/transactions")
    public List<TransactionDto> retrieveAllTransaction(@RequestBody TransactionRequestDto request) {
        if(request.getStartDate() == null || request.getEndDate() == null) {
            throw new InvalidTransactionException("Invalid parameters");
        }
        return tranferService.findAllTransaction(request);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @PostMapping("/transactions/account")
    public List<TransactionDto> retrieveUserAllTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        return tranferService.findAllAccountTransaction(dto.getAccountId(), dto.getType());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transactions/request")
    public TransactionDto requestTransaction(@Valid @RequestBody TransactionDto dto) {
        TransactionDto opt = tranferService.requestTransaction(dto);
        return opt;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transactions/pay")
    public ResponseEntity<String> pay(@RequestBody TransactionDto dto) {
        if(dto.getId() == null || dto.getId() == "" || dto.getOtpCode() == null || dto.getOtpCode() == "") {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        tranferService.pay(dto);
        return ResponseEntity.ok("Transfer money successfully!");
    }

}
