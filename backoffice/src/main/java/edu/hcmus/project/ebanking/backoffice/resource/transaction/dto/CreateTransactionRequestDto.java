package edu.hcmus.project.ebanking.backoffice.resource.transaction.dto;

import edu.hcmus.project.ebanking.backoffice.model.Debt;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionFeeType;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CreateTransactionRequestDto implements Serializable {
    private String source;
    private String target;
    private String content;
    private Double amount;
    private String bankId;
    private TransactionType type;
    @NotNull
    private TransactionFeeType feeType;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(TransactionFeeType feeType) {
        this.feeType = feeType;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
