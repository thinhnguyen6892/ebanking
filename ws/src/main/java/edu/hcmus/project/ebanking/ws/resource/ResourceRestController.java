package edu.hcmus.project.ebanking.ws.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hcmus.project.ebanking.ws.config.exception.BadRequestException;
import edu.hcmus.project.ebanking.ws.config.security.ClientDetails;
import edu.hcmus.project.ebanking.ws.resource.dto.*;
import edu.hcmus.project.ebanking.ws.service.WsService;
import edu.hcmus.project.ebanking.ws.service.SignatureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.ws.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public TransactionDto requestTransaction(@Valid @RequestBody SignatureDto<TransactionRequestDto> dto, HttpServletRequest request, ZoneId clientZoneId) {
        ClientDetails clientDetails = getLoggedClient();
        hashVerify(dto, request, clientZoneId);
        byte[] signature = Base64Utils.decode(dto.getSign().getBytes());
        try {
            signatureService.verifyWithPublicKey(dto.getHash(), signature, clientDetails.getKey());
            switch (dto.getContent().getTransType()) {
                case DEPOSIT: return wsService.depositTransaction(clientDetails.getUsername(), dto.getContent());
                default:
                    return wsService.withDrawTransaction(clientDetails.getUsername(), dto.getContent());
            }

        } catch (Exception e) {
            throw new BadRequestException("Invalid sign");
        }
    }

    @GetMapping(value = "/transaction/sample/privatekey", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<byte[]> getSamplePrivateKey(HttpServletResponse response) throws IOException {
        Path file = signatureService.getSamplePrivateKey().toPath();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());

        return new HttpEntity<byte[]>(Files.readAllBytes(file), headers);
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
