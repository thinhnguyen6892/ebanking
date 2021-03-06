package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;

import javax.validation.constraints.Positive;

public class RSATransactionDto {
    private String account;
    @Positive
    private Integer amount;
    private String content;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
