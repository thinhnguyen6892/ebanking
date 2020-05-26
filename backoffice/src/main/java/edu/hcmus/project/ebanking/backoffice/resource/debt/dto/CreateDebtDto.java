package edu.hcmus.project.ebanking.backoffice.resource.debt.dto;

import edu.hcmus.project.ebanking.data.model.contranst.DebtStatus;

import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;

public class CreateDebtDto implements Serializable {
    private Date createDate;
    private DebtStatus status;
    private String debtor;
    private String content;
    @Positive
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
}
