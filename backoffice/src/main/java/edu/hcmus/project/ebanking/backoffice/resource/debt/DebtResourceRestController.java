package edu.hcmus.project.ebanking.backoffice.resource.debt;

import edu.hcmus.project.ebanking.backoffice.service.DebtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class DebtResourceRestController {

    @Autowired
    private DebtService debtService;

    @GetMapping("/debt")
    public List<DebtDto> getAllDebt() {
        return debtService.GetAllDebt();
    }

    @GetMapping("/debt/{id}")
    public DebtDto findDebt(@Valid @PathVariable int id){
        return debtService.findDebt(id);
    }

    @ApiOperation(value = "View a list of available debt of current log-on user", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/debt/had/{user}")
    public List<DebtDto> findDebtByHolderOrDebtor(@Valid @PathVariable int user){
        return debtService.findDebtbyHolderOrDebtor(user);
    }

    @GetMapping("/debt/debtor/{user}")
    public List<DebtDto> findNewDebtByDebtor(@Valid @PathVariable int user){
        return debtService.findNewDebtByDebtor(user);
    }

    @PostMapping("/debt/create")
    public ResponseEntity<DebtDto> createDebt(@RequestBody DebtDto dto) {
        boolean result = debtService.createDebt(dto);
        return new ResponseEntity<DebtDto>(dto, HttpStatus.OK);
    }

    @PutMapping("/debt/update/{id}")
    public ResponseEntity<DebtDto> updateDebt(@RequestBody DebtDto dto, @PathVariable int id){
        boolean result = debtService.updateDebt(dto, id);
        return new ResponseEntity<DebtDto>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/debt/delete/{id}")
    public ResponseEntity<Void> deleteDebt(@PathVariable int id) {
        boolean result = debtService.deleteDebt(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("debt/updatestatus/{id}")
    public ResponseEntity<DebtDto> updateStatus(@RequestBody DebtDto dto, @PathVariable int id){
        boolean result = debtService.changeStatus(dto, id);
        return new ResponseEntity<DebtDto>(dto, HttpStatus.OK);
    }
}
