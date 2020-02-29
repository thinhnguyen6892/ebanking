package edu.hcmus.project.ebanking.ws.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class TransactionRequest extends AccountRequest {
    @NotNull
    private TransType transType;
    @NotNull
    private Double amount;
    private String note;

    private String sign;

    public TransType getTransType() {
        return transType;
    }

    public void setTransType(TransType transType) {
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
