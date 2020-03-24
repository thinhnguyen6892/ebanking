package edu.hcmus.project.ebanking.ws.resource;

import edu.hcmus.project.ebanking.ws.config.security.ClientDetails;
import edu.hcmus.project.ebanking.ws.model.Bank;
import edu.hcmus.project.ebanking.ws.repository.BankRepository;
import edu.hcmus.project.ebanking.ws.resource.dto.CustomerDto;
import edu.hcmus.project.ebanking.ws.resource.dto.AccountRequestDto;
import edu.hcmus.project.ebanking.ws.resource.dto.TransactionRequestDto;
import edu.hcmus.project.ebanking.ws.service.WsService;
import edu.hcmus.project.ebanking.ws.service.SignatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static edu.hcmus.project.ebanking.ws.model.TransactionType.DEPOSIT;

@Api(value="Account Management Resource")
@RestController
@RequestMapping("/v1.0")
public class ResourceRestController {

    @Autowired
    private WsService wsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private SignatureService signatureService;


    @ApiOperation(value = "Retrieve account information", response = CustomerDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping
    public ResponseEntity<CustomerDto> queryAccountInformation(@Valid @RequestBody AccountRequestDto request) {
        ClientDetails clientDetails = (ClientDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        long requestTime = request.getSubmittedDate().toInstant().toEpochMilli();
        String rawFormula = String.format("%s.%s-%s-%s", requestTime, request.getValidity(), clientDetails.getSecret(), request.getAcd());
        long expires = requestTime + 1000L * (request.getValidity());
        long now = ZonedDateTime.now().toInstant().toEpochMilli();
        if(bCryptPasswordEncoder.matches(rawFormula, request.getHash()) && now > expires) {
            return ResponseEntity.ok(wsService.findAccountInfo(request.getAcd()));
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/test")
    public String queryAccountInformation() {
        Bank bank = bankRepository.getOne("VITI");
        return bCryptPasswordEncoder.encode(String.format("%s.%s",bank.getId(), bank.getSecret()));
    }

    @ApiOperation(value = "Perform transaction processing", response = CustomerDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Transaction successfully completed"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping("/transaction")
    public CustomerDto requestTransaction(@Valid @RequestBody TransactionRequestDto request) {
        ClientDetails clientDetails = (ClientDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        long requestTime = request.getSubmittedDate().toInstant().toEpochMilli();
        String rawFormula = String.format("%s.%s.%s-%s-%s.%s.%s.%s", requestTime, request.getValidity(), request.getAcd(), clientDetails.getSecret(),
                request.getTransType(), request.getAmount(), request.getNote(), request.getSign());
        long expires = requestTime + 1000L * (request.getValidity());
        long now = ZonedDateTime.now().toInstant().toEpochMilli();

        if(bCryptPasswordEncoder.matches(rawFormula, request.getHash()) && now > expires) {
//            signatureService.verifyWithPublicKey()
            return null;
        }
        return null;
    }

    @GetMapping("/transaction/test")
    public TransactionRequestDto requestTransaction() throws InvalidKeySpecException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setAmount(Double.valueOf(10000));
        request.setNote("Test");
        request.setTransType(DEPOSIT);
        request.setAcd("0001582797115906");
        request.setSubmittedDate(ZonedDateTime.now());
        request.setValidity(Long.valueOf(60*5));
        request.setSign(signatureService.signWithPrivateKey(request.toString()));
        request.setHash(bCryptPasswordEncoder.encode(String.format("%s.%s.%s-%s-%s.%s.%s.%s", request.getSubmittedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")), request.getValidity(), request.getAcd(), "mysecret",
                request.getTransType(), request.getAmount(), request.getNote(), request.getSign())));
        return request;
    }

}
