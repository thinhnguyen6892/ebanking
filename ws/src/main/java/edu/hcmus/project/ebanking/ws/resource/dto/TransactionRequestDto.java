package edu.hcmus.project.ebanking.ws.resource.dto;

import edu.hcmus.project.ebanking.data.model.contranst.TransactionFeeType;
import edu.hcmus.project.ebanking.data.model.contranst.TransactionType;
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
