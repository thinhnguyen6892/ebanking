package edu.hcmus.project.ebanking.ws.resource.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hcmus.project.ebanking.ws.model.TransactionFeeType;
import edu.hcmus.project.ebanking.ws.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class TransactionRequestDto extends AccountRequestDto {


    @NotNull
    private TransactionType transType;
    @NotNull
    private TransactionFeeType feeType;
    private Double fee;
    @NotNull
    @Positive
    private Double amount;
    private String note;

}
