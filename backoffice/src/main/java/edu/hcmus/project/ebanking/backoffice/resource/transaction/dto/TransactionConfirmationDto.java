package edu.hcmus.project.ebanking.backoffice.resource.transaction.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TransactionConfirmationDto implements Serializable {

    @NotNull
    @ApiModelProperty(notes = "[Required] Transaction Identify")
    private String id;

    @ApiModelProperty(notes = "[Required] Otp Code")
    @NotBlank
    private String otpCode;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
