package edu.hcmus.project.ebanking.backoffice.resource.transaction;

import edu.hcmus.project.ebanking.backoffice.model.TransactionFeeType;
import edu.hcmus.project.ebanking.backoffice.model.TransactionType;

import java.io.Serializable;
import java.util.Date;

public class TransactionDto implements Serializable {

    private String id;
    private String source;
    private String target;
    private Date created;
    private String content;
    private Double amount;
    private TransactionType type;
    private TransactionFeeType feeType;
    private String otpCode;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public TransactionFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(TransactionFeeType feeType) {
        this.feeType = feeType;
    }
}
