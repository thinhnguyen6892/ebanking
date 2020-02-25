package edu.hcmus.project.ebanking.backoffice.resource.transaction;


import edu.hcmus.project.ebanking.backoffice.model.TransactionType;

import java.io.Serializable;

public class TransactionRequestDto implements Serializable {
    private String accountId;
    private TransactionType type;

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
}
