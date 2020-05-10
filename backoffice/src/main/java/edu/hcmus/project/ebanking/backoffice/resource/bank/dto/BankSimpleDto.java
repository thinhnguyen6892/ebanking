package edu.hcmus.project.ebanking.backoffice.resource.bank.dto;

import edu.hcmus.project.ebanking.data.model.Bank;

import java.io.Serializable;

public class BankSimpleDto implements Serializable {
    private String id;
    private String bankName;

    public BankSimpleDto() {

    }

    public BankSimpleDto(Bank bank) {
        this.id = bank.getId();
        this.bankName = bank.getBankName();
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

}
