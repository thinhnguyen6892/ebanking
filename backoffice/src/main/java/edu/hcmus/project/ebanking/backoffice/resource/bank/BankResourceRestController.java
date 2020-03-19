package edu.hcmus.project.ebanking.backoffice.resource.bank;

import edu.hcmus.project.ebanking.backoffice.model.Bank;
import edu.hcmus.project.ebanking.backoffice.repository.BankRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BankResourceRestController {
    @Autowired
    private BankRepository BankRepository;


    //@Secured("ROLE_USER")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PutMapping("/bank/update/{id}")
    public ResponseEntity<BankDto> updateBank(@RequestBody BankDto dto, @PathVariable String id){
        Optional<Bank> BankOp = BankRepository.findById(id);
        if(BankOp.isPresent()) {
            Bank upBank = BankOp.get();
            upBank.setBankName(dto.getBankName());
            upBank.setAddress(dto.getAddress());
            upBank.setEmail(dto.getEmail());
            upBank.setPhone(dto.getPhone());
            upBank.setStatus(dto.getStatus());
            upBank.setKey(dto.getKey());
            upBank = BankRepository.save(upBank);
            return new ResponseEntity<BankDto>(dto, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Bank not found");
    }

    @DeleteMapping("/bank/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        Optional<Bank> deBank = BankRepository.findById(id);
        if(deBank.isPresent())
        {
            BankRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Bank not found");
    }
}
