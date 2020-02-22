package edu.hcmus.project.ebanking.backoffice.resource.debt;

import java.io.Serializable;
import java.sql.Timestamp;

public class DebtDto implements Serializable {
    private int id;
    private String type;
    private Timestamp createDate;
    private Timestamp endDate;
    private Boolean status;
    private int holder;
    private int debtor;
    private String debtor_acc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getHolder() {
        return holder;
    }

    public void setHolder(int holder) {
        this.holder = holder;
    }

    public int getDebtor() {
        return debtor;
    }

    public void setDebtor(int debtor) {
        this.debtor = debtor;
    }

    public String getDebtor_acc() {
        return debtor_acc;
    }

    public void setDebtor_acc(String debtor_acc) {
        this.debtor_acc = debtor_acc;
    }
}
