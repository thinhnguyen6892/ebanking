package edu.hcmus.project.ebanking.ws.resource.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class HashableDto<T extends BaseRequestDto> {

    @NotNull
    private String hash;

    @NotNull
    private T content;
}
