package edu.hcmus.project.ebanking.ws.resource.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BankDto implements Serializable {

    private String id;
    private String refSecretKey;


    public BankDto(String id, String refSecretKey) {
        this.id = id;
        this.refSecretKey = refSecretKey;
    }
}
