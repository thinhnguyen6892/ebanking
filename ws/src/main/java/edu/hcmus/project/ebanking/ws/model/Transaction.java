package edu.hcmus.project.ebanking.ws.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
public class Transaction {
    private String id;
    private ZonedDateTime date;
    private TransactionType type;
    private String source;
    private String target;
    private String content;
    private double amount;
    private TransactionStatus status;

    private String otpCode;
    private Long validity;

    private TransactionFeeType feeType;

    private Bank reference;


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
    @Column(name = "date")
    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Basic
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Basic
    @Column(name = "source")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Basic
    @Column(name = "target")
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Basic
    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Basic
    @Column(name = "otp_code")
    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    @Basic
    @Column(name = "validity")
    public Long getValidity() {
        return validity;
    }

    public void setValidity(Long validity) {
        this.validity = validity;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type")
    public TransactionFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(TransactionFeeType feeType) {
        this.feeType = feeType;
    }

    @ManyToOne
    @JoinColumn(name = "reference", referencedColumnName = "id", nullable = false)
    public Bank getReference() {
        return reference;
    }

    public void setReference(Bank reference) {
        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(status,that.status) &&
                Objects.equals(id, that.id) &&
                Objects.equals(date, that.date) &&
                Objects.equals(type, that.type) &&
                Objects.equals(source, that.source) &&
                Objects.equals(target, that.target) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, type, source, target, content, amount, status);
    }

}