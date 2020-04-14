package edu.hcmus.project.ebanking.ws.resource.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hcmus.project.ebanking.ws.model.TransactionFeeType;
import edu.hcmus.project.ebanking.ws.model.TransactionType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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



    public TransactionType getTransType() {
        return transType;
    }

    public void setTransType(TransactionType transType) {
        this.transType = transType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return transType.toString() + amount + note + getAccId();
    }

    public TransactionFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(TransactionFeeType feeType) {
        this.feeType = feeType;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
