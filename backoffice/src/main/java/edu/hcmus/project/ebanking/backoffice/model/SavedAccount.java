package edu.hcmus.project.ebanking.backoffice.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saved_account", schema = "ebanking_db", catalog = "")
public class SavedAccount {
    private int id;
    private String nameSuggestion;
    private String bankId;
    private String accountId;
    private String firstName;
    private String lastName;
    private User owner;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name_suggestion")
    public String getNameSuggestion() {
        return nameSuggestion;
    }

    public void setNameSuggestion(String nameSuggestion) {
        this.nameSuggestion = nameSuggestion;
    }

    @Basic
    @Column(name = "bank_id")
    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    @Basic
    @Column(name = "account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedAccount that = (SavedAccount) o;
        return id == that.id &&
                Objects.equals(nameSuggestion, that.nameSuggestion) &&
                Objects.equals(bankId, that.bankId) &&
                Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nameSuggestion, bankId, accountId);
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "id", nullable = false)
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
