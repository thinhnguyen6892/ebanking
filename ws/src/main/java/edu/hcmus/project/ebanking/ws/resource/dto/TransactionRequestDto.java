package edu.hcmus.project.ebanking.ws.resource.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.hcmus.project.ebanking.ws.model.TransactionType;

import javax.validation.constraints.NotNull;

public class TransactionRequestDto extends AccountRequestDto {
    @NotNull
    private TransactionType transType;
    @NotNull
    private Double amount;
    private String note;

    private String sign;

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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return transType.toString() + amount + note + getAcd();
    }
}
