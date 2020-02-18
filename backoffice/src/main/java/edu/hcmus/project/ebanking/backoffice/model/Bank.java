package edu.hcmus.project.ebanking.backoffice.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bank")
public class Bank {
    private String id;
    private String bankName;
    private String address;
    private String email;
    private String phone;
    private Boolean status;
    private String key;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "bank_name")
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Basic
    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "api_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bank bank = (Bank) o;
        return Objects.equals(id, bank.id) &&
                Objects.equals(bankName, bank.bankName) &&
                Objects.equals(address, bank.address) &&
                Objects.equals(email, bank.email) &&
                Objects.equals(phone, bank.phone) &&
                Objects.equals(status, bank.status) &&
                Objects.equals(key, bank.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bankName, address, email, phone, status, key);
    }
}
