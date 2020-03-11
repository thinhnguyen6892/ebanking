package edu.hcmus.project.ebanking.backoffice.resource.receiver.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class CreateReceiverDto implements Serializable {
    private String nameSuggestion;
    @ApiModelProperty(notes = "Leave it empty if the account is on our system")
    private String bankId;

    @NotNull
    private String accountId;

    public String getNameSuggestion() {
        return nameSuggestion;
    }

    public void setNameSuggestion(String nameSuggestion) {
        this.nameSuggestion = nameSuggestion;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
