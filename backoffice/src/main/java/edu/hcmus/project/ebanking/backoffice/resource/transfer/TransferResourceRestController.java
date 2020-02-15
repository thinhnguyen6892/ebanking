package edu.hcmus.project.ebanking.backoffice.resource.transfer;

import edu.hcmus.project.ebanking.backoffice.service.TranferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TransferResourceRestController {

    @Autowired
    private TranferService tranferService;


    @GetMapping("/transactions")
    public List<TransactionDto> retrieveAllTransaction() {
        return tranferService.findAllTransaction();
    }

    @GetMapping("/transactions/account/{id}")
    public List<TransactionDto> retrieveAllTransaction(@PathVariable String id) {
        return tranferService.findAllAccountTransaction(id);
    }


    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransactionDto dto) {
        tranferService.send(dto);
        return ResponseEntity.ok("Transfer money successfully!");
    }

}
