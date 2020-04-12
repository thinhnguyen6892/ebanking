package edu.hcmus.project.ebanking.backoffice.resource.debt;

import edu.hcmus.project.ebanking.backoffice.resource.debt.dto.*;
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

    @ApiOperation(value = "1.2 [User] Debt Information By ID. ", response = DebtDto.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public DebtDto findDebt(@Valid @PathVariable int id){
        return debtService.findDebt(id);
    }

    @ApiOperation(value = "1.5.2 [User] Debt Information By Holder or Debtor, 0: All; 1: Holder; 2: Debtor. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/holderanddebtor/{type}")
    public List<DebtDto> findDebtByHolderOrDebtor(@Valid @PathVariable int type){
        return debtService.findDebtbyHolderOrDebtor(type);
    }

    @ApiOperation(value = "1.5.1 [User] New Debt Information By Debtor. ", response = List.class)
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/debtor")
    public List<DebtDto> findNewDebtByDebtor(){
        return debtService.findNewDebtByDebtor();
    }

    @ApiOperation(value = "1.5.1 [User] Search Debtor Information By Account ID. ", response = List.class)
    @GetMapping("/search/{account}")
    public List<DebtUserDto> search(@PathVariable String account) {
        return debtService.search(account);
    }

    @ApiOperation(value = "1.5.1 [User] Create Debt Information. ", response = CreateDebtDto.class)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<CreateDebtDto> createDebt(@RequestBody CreateDebtDto dto) {
        debtService.createDebt(dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.7 [User] Update Debt Information. ", response = CreateDebtDto.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CreateDebtDto> updateDebt(@RequestBody CreateDebtDto dto, @PathVariable int id){
        boolean result = debtService.updateDebt(dto, id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "1.5.3 [User] Cancel Debt Information with write reason in content. ")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> CancelDebt(@RequestBody CancelDto dto, @Valid @PathVariable int id){
        debtService.CancelDebt(dto, id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "1.5.4 Thanh toan nhac no ", response = DebtPaymentDto.class)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/pay/{id}")
    public ResponseEntity<DebtPaymentDto> pay(@PathVariable @NotNull Integer id){
        DebtPaymentDto result = debtService.pay(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
