package edu.hcmus.project.ebanking.backoffice.resource.transaction.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionType;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class TransactionQueryDto implements Serializable {
    private String accountId;

    @ApiModelProperty(notes = "Transaction Type. [ DEPOSIT, WITHDRAW, TRANSFER, PAYMENT ]")
    private TransactionType type;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime endDate;

    @ApiModelProperty(notes = "Filter by bank (Only apply for administrator).")
    private String bankId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
