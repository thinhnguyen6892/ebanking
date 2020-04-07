package edu.hcmus.project.ebanking.backoffice.resource.debt;

import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.CreateDebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.DebtDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.DebtPaymentDto;
import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.DebtUserDto;
import edu.hcmus.project.ebanking.backoffice.service.DebtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/debt")
public class DebtResourceRestController {

    @Autowired
    private DebtService debtService;

    @ApiOperation(value = "1.1 [User] Debt All Information. ", response = List.class)
    @GetMapping
    public List<DebtDto> getAllDebt() {
        return debtService.GetAllDebt();
    }

    @ApiOperation(value = "1.2 [User] Debt Information By ID. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public DebtDto findDebt(@Valid @PathVariable int id){
        return debtService.findDebt(id);
    }

    @ApiOperation(value = "1.3 [User] Debt Information By Holder or Debtor, 0: All; 1: Holder; 2: Debtor. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/holderanddebtor/{type}")
    public List<DebtDto> findDebtByHolderOrDebtor(@Valid @PathVariable int type){
        return debtService.findDebtbyHolderOrDebtor(type);
    }

    @ApiOperation(value = "1.4 [User] New Debt Information By Debtor. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/debtor")
    public List<DebtDto> findNewDebtByDebtor(){
        return debtService.findNewDebtByDebtor();
    }

    @ApiOperation(value = "1.5 [User] Search Debtor Information By Account ID. ", response = List.class)
    @GetMapping("/search/{account}")
    public List<DebtUserDto> search(@PathVariable String account) {
        return debtService.search(account);
    }

    @ApiOperation(value = "1.6 [User] Create Debt Information. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<CreateDebtDto> createDebt(@RequestBody CreateDebtDto dto) {
        boolean result = debtService.createDebt(dto);
        return new ResponseEntity<CreateDebtDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.7 [User] Update Debt Information. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CreateDebtDto> updateDebt(@RequestBody CreateDebtDto dto, @PathVariable int id){
        boolean result = debtService.updateDebt(dto, id);
        return new ResponseEntity<CreateDebtDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.7 [User] Cancel Debt Information. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/Cancel/{id}")
    public ResponseEntity<CreateDebtDto> CancelDebt(@RequestBody CreateDebtDto dto, @PathVariable int id){
        boolean result = debtService.CancelDebt(dto, id);
        return new ResponseEntity<CreateDebtDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.8 [User] Delete Debt. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDebt(@PathVariable int id) {
        boolean result = debtService.deleteDebt(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "1.9 [User] Update Status Debt. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/updatestatus/{id}")
    public ResponseEntity<CreateDebtDto> updateStatus(@RequestBody CreateDebtDto dto, @PathVariable int id){
        boolean result = debtService.changeStatus(dto, id);
        return new ResponseEntity<CreateDebtDto>(dto, HttpStatus.OK);
    }


    @ApiOperation(value = "1.5.4 Thanh toan nhac no ", response = DebtPaymentDto.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/pay/{id}")
    public ResponseEntity<DebtPaymentDto> pay(@PathVariable @NotNull Integer id){
        DebtPaymentDto result = debtService.pay(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
