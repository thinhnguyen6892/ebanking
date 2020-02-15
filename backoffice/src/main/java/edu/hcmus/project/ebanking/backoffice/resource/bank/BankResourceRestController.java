package edu.hcmus.project.ebanking.backoffice.resource.bank;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.resource.bank.BankDto;
import edu.hcmus.project.ebanking.backoffice.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class BankResourceRestController {
    @Autowired
    private BankRepository BankRepository;

    @GetMapping("/bank")
    public List<Bank> getAllBank() {
        return BankRepository.findAll();
    }

    @GetMapping("/bank/{id}")
    public Bank getByid(@PathVariable long id){return BankRepository.findById(id);}

    @PostMapping("/bank/create")
    public ResponseEntity<BankDto> createUser(@RequestBody BankDto dto) {
        Bank newBank = new Bank();
        newBank.setId(dto.getId());
        newBank.setBankName(dto.getBankName());
        newBank.setAddress(dto.getAddress());
        newBank = BankRepository.save(newBank);
        return new ResponseEntity<BankDto>(dto, HttpStatus.OK);
    }
}
