package edu.hcmus.project.ebanking.ws.resource.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureDto<T extends BaseRequestDto> extends HashableDto<T> {
    private String sign;
}