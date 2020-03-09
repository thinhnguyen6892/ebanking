package edu.hcmus.project.ebanking.backoffice.resource.debt;

import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;

import java.io.Serializable;
import java.util.Date;


public class DebtDto implements Serializable {
    private Date createDate;
    private DebtStatus status;
    private Long holder;
    private Long debtor;
    private String debtor_acc;

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

    public Long getDebtor() {
        return debtor;
    }

    public void setDebtor(Long debtor) {
        this.debtor = debtor;
    }

    public String getDebtor_acc() {
        return debtor_acc;
    }

    public void setDebtor_acc(String debtor_acc) {
        this.debtor_acc = debtor_acc;
    }
}
