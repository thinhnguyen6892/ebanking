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
    private Account debtor;
    private String content;
    private Double amount;
    private String note;
    private Transaction paymentRef;


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
    @JoinColumn (name = "debtor", referencedColumnName = "account_id", nullable = false)
    public Account getDebtor() {
        return debtor;
    }

    public void setDebtor(Account debtor) {
        this.debtor = debtor;
    }



    @Basic
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
                Objects.equals(content, debt.content) &&
                Objects.equals(amount, debt.amount) &&
                Objects.equals(note, debt.note) &&
                Objects.equals(paymentRef, debt.paymentRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createDate, status, holder, debtor, content, amount, note, paymentRef);
    }



    @ManyToOne
    @JoinColumn(name = "payment_ref", referencedColumnName = "id")
    public Transaction getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(Transaction paymentRef) {
        this.paymentRef = paymentRef;
    }

    @Basic
    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
