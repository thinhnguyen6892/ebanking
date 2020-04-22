package edu.hcmus.project.ebanking.ws.resource.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class BaseRequestDto implements Serializable, Cloneable  {
    private String clientKey;

    @NotNull
    private Long validity;

    @Override
    public BaseRequestDto clone() throws CloneNotSupportedException {
        return (BaseRequestDto) super.clone();
    }
}
