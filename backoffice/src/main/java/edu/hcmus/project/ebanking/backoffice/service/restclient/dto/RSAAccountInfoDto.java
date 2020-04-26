package edu.hcmus.project.ebanking.backoffice.service.restclient.dto;

public class RSAAccountInfoDto {
    private String account;
    private String name;
    private String bank;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
