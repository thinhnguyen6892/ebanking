package edu.hcmus.project.ebanking.ws.resource.dto;

import edu.hcmus.project.ebanking.data.model.contranst.SignType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
public class ClientRegisterDto implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String address;
    private String secret;
    private String apiKey;
    private String accountEndpoint;
    private String transactionEndpoint;
    private SignType signType;
    private MultipartFile publicKey;
}
