package edu.hcmus.project.ebanking.backoffice.resource.bank;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.repository.BankRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import edu.hcmus.project.ebanking.backoffice.resource.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BankResourceRestController {
    @Autowired
    private BankRepository BankRepository;

    @GetMapping("/bank")
    public List<Bank> getAllBank() {
        return BankRepository.findAll();
    }

    @GetMapping("/bank/{id}")
    public BankDto findBank(@PathVariable String id){
        Optional<Bank> BankOp = BankRepository.findById(id);
        if(BankOp.isPresent()){
            Bank bank = BankOp.get();
            BankDto dto = new BankDto();
            dto.setId(bank.getId());
            dto.setBankName(bank.getBankName());
            dto.setAddress(bank.getAddress());
            dto.setEmail(bank.getEmail());
            dto.setPhone(bank.getPhone());
            dto.setStatus(bank.getStatus());
            dto.setKey(bank.getKey());
            return dto;
        }
        throw new ResourceNotFoundException("Bank not found");
    }

    @PostMapping("/bank/create")
    public ResponseEntity<BankDto> createBank(@RequestBody BankDto dto) {
        Bank newBank = new Bank();
        newBank.setId(dto.getId());
        newBank.setBankName(dto.getBankName());
        newBank.setAddress(dto.getAddress());
        newBank.setEmail(dto.getEmail());
        newBank.setPhone(dto.getPhone());
        newBank.setStatus(dto.getStatus());
        newBank.setKey(dto.getKey());
        newBank = BankRepository.save(newBank);
        return new ResponseEntity<BankDto>(dto, HttpStatus.OK);
    }
}
