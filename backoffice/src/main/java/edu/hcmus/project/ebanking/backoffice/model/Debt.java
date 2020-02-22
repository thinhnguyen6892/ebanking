package edu.hcmus.project.ebanking.backoffice.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Debt {
    private int id;
    private String type;
    private Timestamp createDate;
    private Timestamp endDate;
    private Boolean status;
    private Account holder;
    private Account debtor;
    private Account debtor_acc;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "create_date")
    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "end_date")
    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Column(name = "status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn (name = "holder", referencedColumnName = "owner_id", nullable = false)
    public Account getHolder() {
        return holder;
    }

    public void setHolder(Account holder) {
        this.holder = holder;
    }

    @ManyToOne
    @JoinColumn (name = "debtor", referencedColumnName = "owner_id", nullable = false)
    public Account getDebtor() {
        return debtor;
    }

    public void setDebtor(Account debtor) {
        this.debtor = debtor;
    }

    @ManyToOne
    @JoinColumn (name = "debtor_acc", referencedColumnName = "account_id", nullable = false)
    public Account getDebtor_acc() {
        return debtor_acc;
    }

    public void setDebtor_acc(Account debtor_acc) {
        this.debtor_acc = debtor_acc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return id == debt.id &&
                Objects.equals(type, debt.type) &&
                Objects.equals(createDate, debt.createDate) &&
                Objects.equals(endDate, debt.endDate) &&
                Objects.equals(status, debt.status) &&
                Objects.equals(holder, debt.holder) &&
                Objects.equals(debtor, debt.debtor) &&
                Objects.equals(debtor_acc, debt.debtor_acc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, createDate, endDate, status, holder, debtor, debtor_acc);
    }


}
