package edu.hcmus.project.ebanking.backoffice.resource.account.dto;

import edu.hcmus.project.ebanking.data.model.contranst.TransactionFeeType;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class DepositAccount implements Serializable {

    @ApiModelProperty(notes = "Deposit by account id. It will query by default.")
    private String accountId;

    @ApiModelProperty(notes = "Deposit by username. It will get Payment account by default")
    private String username;

    @NotNull
    private Double amount;

    @NotNull
    private String content;

    @ApiModelProperty(notes = "Fee Type.  [SENDER | RECEIVER]")
    private TransactionFeeType feeType;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TransactionFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(TransactionFeeType feeType) {
        this.feeType = feeType;
    }
}
