package edu.hcmus.project.ebanking.ws.model;

import org.hibernate.annotations.GenericGenerator;

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
    private byte[] key;
    private String secret;
    private SignType signType;
    private String apiKey;
    private String accountEndpoint;
    private String transactionEndpoint;

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
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

    @Lob
    @Column(name = "public_key")
    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    @Basic
    @Column(name = "secret")
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Basic
    @Column(name = "sign_type")
    @Enumerated(EnumType.ORDINAL)
    public SignType getSignType() {
        return signType;
    }

    public void setSignType(SignType signType) {
        this.signType = signType;
    }

    @Basic
    @Column(name = "api_key")
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Basic
    @Column(name = "accountEndpoint")
    public String getAccountEndpoint() {
        return accountEndpoint;
    }

    public void setAccountEndpoint(String accountEndpoint) {
        this.accountEndpoint = accountEndpoint;
    }

    @Basic
    @Column(name = "transactionEndpoint")
    public String getTransactionEndpoint() {
        return transactionEndpoint;
    }

    public void setTransactionEndpoint(String transactionEndpoint) {
        this.transactionEndpoint = transactionEndpoint;
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
