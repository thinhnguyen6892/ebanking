package edu.hcmus.project.ebanking.backoffice.resource.debt;

import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.repository.DebtRepository;
import edu.hcmus.project.ebanking.backoffice.resource.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class DebtResourceRestController {

    @Autowired
    private DebtRepository DebtRepository;

    @GetMapping("/debt")
    public List<Debt> getAllDebt() {
        return DebtRepository.findAll();
    }

    @GetMapping("/debt/{id}")
    public DebtDto findDebt(@PathVariable int id){
        Optional<Debt> DebtOp = DebtRepository.findById(id);
        if(DebtOp.isPresent()){
            Debt debt = DebtOp.get();
            DebtDto dto = new DebtDto();
            dto.setId(debt.getId());
            dto.setType(debt.getType());
            dto.setCreateDate(debt.getCreateDate());
            dto.setEndDate(debt.getEndDate());
            dto.setStatus(debt.getStatus());
            return dto;
        }
        throw new ResourceNotFoundException("Debt not found");
    }

    @PostMapping("/debt/create")
    public ResponseEntity<DebtDto> createBank(@RequestBody DebtDto dto) {
        Optional<Debt> Debtop = DebtRepository.findById(dto.getId());
        if(!Debtop.isPresent()) {
            Debt newDebt = new Debt();
            newDebt.setId(dto.getId());
            newDebt.setType(dto.getType());
            newDebt.setCreateDate(dto.getCreateDate());
            newDebt.setEndDate(dto.getEndDate());
            newDebt.setStatus(dto.getStatus());
            newDebt = DebtRepository.save(newDebt);
            return new ResponseEntity<DebtDto>(dto, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Debt have existed");
    }

    @PutMapping("/debt/update/{id}")
    public ResponseEntity<DebtDto> updateBank(@RequestBody DebtDto dto, @PathVariable int id){
        Optional<Debt> BankOp = DebtRepository.findById(id);
        if(BankOp.isPresent()) {
            Debt upDebt = BankOp.get();
            upDebt.setType(dto.getType());
            upDebt.setCreateDate(dto.getCreateDate());
            upDebt.setEndDate(dto.getEndDate());
            upDebt.setStatus(dto.getStatus());
            upDebt = DebtRepository.save(upDebt);
            return new ResponseEntity<DebtDto>(dto, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Debt not found");
    }

    @DeleteMapping("/debt/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Optional<Debt> deBank = DebtRepository.findById(id);
        if(deBank.isPresent())
        {
            DebtRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("Debt not found");
    }
}
