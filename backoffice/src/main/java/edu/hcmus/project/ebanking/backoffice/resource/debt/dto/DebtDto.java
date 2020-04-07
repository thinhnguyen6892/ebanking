package edu.hcmus.project.ebanking.backoffice.resource.debt.dto;

import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;

import java.io.Serializable;
import java.util.Date;


public class DebtDto implements Serializable {
    private Date createDate;
    private DebtStatus status;
    private Long holder;
    private String holderFirstName;
    private String holderLastName;
    private String debtor;
    private String debtorFirstName;
    private String debtorLastName;
    private String content;
    private Double amount;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public DebtStatus getStatus() {
        return status;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }

    public Long getHolder() {
        return holder;
    }

    public void setHolder(Long holder) {
        this.holder = holder;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtor) {
        this.debtor = debtor;
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

    public String getHolderFirstName() {
        return holderFirstName;
    }

    public void setHolderFirstName(String holderFirstName) {
        this.holderFirstName = holderFirstName;
    }

    public String getHolderLastName() {
        return holderLastName;
    }

    public void setHolderLastName(String holderLastName) {
        this.holderLastName = holderLastName;
    }

    public String getDebtorFirstName() {
        return debtorFirstName;
    }

    public void setDebtorFirstName(String debtorFirstName) {
        this.debtorFirstName = debtorFirstName;
    }

    public String getDebtorLastName() {
        return debtorLastName;
    }

    public void setDebtorLastName(String debtorLastName) {
        this.debtorLastName = debtorLastName;
    }
}
