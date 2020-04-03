package edu.hcmus.project.ebanking.backoffice.resource.transaction;

import edu.hcmus.project.ebanking.backoffice.model.User;
import edu.hcmus.project.ebanking.backoffice.resource.exception.InvalidTransactionException;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.CreateTransactionRequestDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionConfirmationDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionDto;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionQueryDto;
import edu.hcmus.project.ebanking.backoffice.security.jwt.JwtTokenUtil;
import edu.hcmus.project.ebanking.backoffice.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionResourceRestController {

    @Autowired
    private TransactionService tranferService;

    @ApiOperation(value = "[Administrator] Retrieve all transaction including filtering by bank id.", response = List.class)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public Page<TransactionDto> retrieveAllTransaction(TransactionQueryDto request,
                                                       @SortDefault.SortDefaults({
                                                               @SortDefault(sort = "date", direction = Sort.Direction.DESC)
                                                       }) Pageable pageable) {
        if(request.getStartDate() == null || request.getEndDate() == null) {
            throw new InvalidTransactionException("Invalid parameters");
        }
        return tranferService.findAllTransaction(request, pageable);
    }

    @ApiOperation(value = "1.6 [User - Employee] View account transaction. Filter by TransactionType [DEPOSIT- Nhan Tien, WITHDRAW - Rut Tien, TRANSFER - Chuyen Tien, PAYMENT - Thanh Toan No]", response = List.class)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'STAFF')")
    @GetMapping("/account")
    public Page<TransactionDto> retrieveUserAllTransaction(@Valid TransactionQueryDto dto,
                                                           @SortDefault.SortDefaults({
                                                                   @SortDefault(sort = "date", direction = Sort.Direction.DESC)
                                                           }) Pageable pageable) {
        User user = JwtTokenUtil.getLoggedUser();
        //TODO
        String role = user.getRole().getRoleId();
        return "USER".equalsIgnoreCase(role) ? tranferService.findUserAccountTransaction(user, dto.getAccountId(), dto.getType(), pageable) : tranferService.findAllAccountTransaction(dto.getAccountId(), dto.getType(), pageable);
    }

    @ApiOperation(value = "1.4.1_01 [USER] Request a new transaction", response = TransactionDto.class)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/request")
    public TransactionDto requestTransaction(@Valid @RequestBody CreateTransactionRequestDto dto) {
        TransactionDto opt = tranferService.requestTransaction(JwtTokenUtil.getLoggedUser(), dto);
        return opt;
    }

    @ApiOperation(value = "1.4.1_02 [USER] Confirm to complete the transaction")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/confirmation")
    public ResponseEntity<TransactionDto> pay(@Valid @RequestBody TransactionConfirmationDto dto) {
        return ResponseEntity.ok().body(tranferService.confirm(JwtTokenUtil.getLoggedUser(), dto));
    }

}
