package edu.hcmus.project.ebanking.backoffice.resource.transaction;

import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TransferResourceRestController {

    @Autowired
    private TransactionService tranferService;


    @GetMapping("/transactions")
    public List<TransactionDto> retrieveAllTransaction() {
        return tranferService.findAllTransaction();
    }

    @PostMapping("/transactions/account")
    public List<TransactionDto> retrieveAllTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        return tranferService.findAllAccountTransaction(dto.getAccountId(), dto.getType());
    }

    @PostMapping("/transactions/request")
    public TransactionDto requestTransaction(@Valid @RequestBody TransactionDto dto) {
        TransactionDto opt = tranferService.requestTransaction(dto);
        return opt;
    }

    @PostMapping("/transactions/pay")
    public ResponseEntity<String> pay(@RequestBody TransactionDto dto) {
        if(dto.getId() == null || dto.getId() == "" || dto.getOtpCode() == null || dto.getOtpCode() == "") {
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        tranferService.pay(dto);
        return ResponseEntity.ok("Transfer money successfully!");
    }

}
