package edu.hcmus.project.ebanking.ws.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hcmus.project.ebanking.ws.config.exception.BadRequestException;
import edu.hcmus.project.ebanking.ws.config.security.ClientDetails;
import edu.hcmus.project.ebanking.data.model.Bank;
import edu.hcmus.project.ebanking.ws.resource.dto.*;
import edu.hcmus.project.ebanking.ws.service.TokenProvider;
import edu.hcmus.project.ebanking.ws.service.WsService;
import edu.hcmus.project.ebanking.ws.service.SignatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Api(value="Account Management Resource")
@RestController
@RequestMapping("/v1.0")
public class ResourceRestController {

    @Autowired
    private WsService wsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TokenProvider tokenProvider;


    @ApiOperation(value = "Retrieve account information", response = CustomerDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved information"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping("/account")
    public ResponseEntity<CustomerDto> queryAccountInformation(@Valid @RequestBody HashableDto<AccountRequestDto> dto, HttpServletRequest request, ZoneId clientZoneId) throws JsonProcessingException {
        hashVerify(dto, request, clientZoneId);
        return ResponseEntity.ok(wsService.findAccountInfo(dto.getContent().getAccId()));
    }
    @GetMapping("/account/request/sample")
    public HashableDto<AccountRequestDto> queryAccountInformation(@RequestBody AccountRequestDto content) throws JsonProcessingException {
        HashableDto<AccountRequestDto> dto = new HashableDto<>();
        ClientDetails clientDetails = (ClientDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        content.setClientKey(clientDetails.getSecret());
        dto.setHash(bCryptPasswordEncoder.encode(mapper.writeValueAsString(content)));
        dto.setContent(content);
        return dto;
    }

    @ApiOperation(value = "Perform transaction processing", response = CustomerDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Transaction successfully completed"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @PostMapping("/transaction")
    public SignatureDto<TransactionDto> requestTransaction(@Valid @RequestBody SignatureDto<TransactionRequestDto> dto, HttpServletRequest request, ZoneId clientZoneId) {
        ClientDetails clientDetails = getLoggedClient();
        hashVerify(dto, request, clientZoneId);

        try {
            BaseRequestDto contentDto = dto.getContent();
            String contentAsString = mapper.writeValueAsString(contentDto);
            byte[] signature = Base64Utils.decode(dto.getSign().getBytes("UTF-8"));
            signatureService.verifyWithPublicKey(clientDetails.getSignType(), contentAsString, signature, clientDetails.getKey());
            TransactionDto content = null;
            switch (dto.getContent().getTransType()) {
                case DEPOSIT: content = wsService.depositTransaction(clientDetails.getUsername(), dto.getContent()); break;
                default:
                    content = wsService.withDrawTransaction(clientDetails.getUsername(), dto.getContent()); break;
            }
            TransactionDto hashContent = content.clone();
            hashContent.setClientKey(clientDetails.getSecret());
            SignatureDto<TransactionDto> result = new SignatureDto<>();
            result.setContent(content);
            String hash = bCryptPasswordEncoder.encode(mapper.writeValueAsString(hashContent));
            result.setHash(hash);
            result.setSign(signatureService.signWithPrivateKey(hash));
            return result;
        } catch (Exception e) {
            throw new BadRequestException("Invalid sign");
        }
    }

    @GetMapping("/transaction/request/sample")
    public SignatureDto<TransactionRequestDto> requestTransactionSAmple(@Valid @RequestBody TransactionRequestDto content) {
        ClientDetails clientDetails = getLoggedClient();
        SignatureDto<TransactionRequestDto> result = new SignatureDto<>();
        content.setClientKey(clientDetails.getSecret());
        content.setValidity(10000l);
        result.setContent(content);
        String hash = null;
        try {
            hash = bCryptPasswordEncoder.encode(mapper.writeValueAsString(content));
            result.setHash(hash);
            result.setSign(signatureService.signWithPrivateKey(hash));
            return result;
        } catch (Exception e) {
            throw new BadRequestException("Invalid sign");
        }
    }

/*    @GetMapping(value = "/publicKey", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<byte[]> getSamplePrivateKey(HttpServletResponse response) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + "publicKey.der");

        return new HttpEntity<byte[]>(signatureService.getPublicKey(), headers);
    }*/

    @PostMapping("/register")
    public BankDto registerNewClient(@ModelAttribute ClientRegisterDto dto) {
        try {
            Bank bank = wsService.createNewRefBank(dto);
            return new BankDto(bank.getId(), tokenProvider.computeSignature(bank.getId(), bank.getSecret()));
        } catch (IOException e) {
            throw new BadRequestException("Cannot load the public key.");
        }
    }

    @PostMapping("/client/update")
    public ResponseEntity updatePublicKey(@RequestParam MultipartFile key) {
        try {
            ClientDetails details = getLoggedClient();
            wsService.updateRefBankKey(details.getUsername(), key);
            return ResponseEntity.ok().body("Updated");
        } catch (IOException e) {
            throw new BadRequestException("Cannot load the public key.");
        }
    }



    private ClientDetails getLoggedClient() {
        return (ClientDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
    }

    private void hashVerify(HashableDto<?> dto, HttpServletRequest request, ZoneId clientZoneId) {
        ClientDetails clientDetails = getLoggedClient();
        LocalDateTime requestLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getSession().getLastAccessedTime()), clientZoneId);
        long requestTime = requestLdt.atZone(clientZoneId).toInstant().toEpochMilli();
        long expires = requestTime + 1000L * (dto.getContent().getValidity());
        long now = ZonedDateTime.now().toInstant().toEpochMilli();
        BaseRequestDto contentDto = dto.getContent();
        contentDto.setClientKey(clientDetails.getSecret());
        try {
            String rawContent = mapper.writeValueAsString(contentDto);
            if(!bCryptPasswordEncoder.matches(rawContent, dto.getHash()) || now > expires) {
                throw new BadRequestException("Request is invalid or expired!");
            }
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid json content.");
        }
    }

}
