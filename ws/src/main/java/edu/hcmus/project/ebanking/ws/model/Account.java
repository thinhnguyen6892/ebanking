package edu.hcmus.project.ebanking.ws.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
public class Account {

    private String accountId;
    private AccountType type;
    private Double balance = new Double(0);
    private Date createDate = new Date();
    private Date expired;
    private Boolean status;
    private User owner;



//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @GenericGenerator(
            name = "account_seq",
            strategy = "edu.hcmus.project.ebanking.backoffice.generator.TimestampGenerator"
    )
    @Column(name = "account_id")
    public String getAccountId() {
        return accountId;
    }
;
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Basic
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    @Basic
    @Column(name = "balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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
    @Column(name = "expired")
    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Basic
    @Column(name = "status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId) &&
                Objects.equals(type, account.type) &&
                Objects.equals(balance, account.balance) &&
                Objects.equals(createDate, account.createDate) &&
                Objects.equals(expired, account.expired) &&
                Objects.equals(status, account.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, type, balance, createDate, expired, status);
    }
}
