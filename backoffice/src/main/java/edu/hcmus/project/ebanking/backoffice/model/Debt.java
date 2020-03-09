package edu.hcmus.project.ebanking.backoffice.model;

import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class Debt {
    private int id;
    private Date createDate;
    private DebtStatus status;
    private User holder;
    private User debtor;
    private Account debtor_acc;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public DebtStatus getStatus() {
        return status;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn (name = "holder", referencedColumnName = "id", nullable = false)
    public User getHolder() {
        return holder;
    }

    public void setHolder(User holder) {
        this.holder = holder;
    }

    @ManyToOne
    @JoinColumn (name = "debtor", referencedColumnName = "id", nullable = false)
    public User getDebtor() {
        return debtor;
    }

    public void setDebtor(User debtor) {
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
                Objects.equals(createDate, debt.createDate) &&
                Objects.equals(status, debt.status) &&
                Objects.equals(holder, debt.holder) &&
                Objects.equals(debtor, debt.debtor) &&
                Objects.equals(debtor_acc, debt.debtor_acc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createDate, status, holder, debtor, debtor_acc);
    }

}
