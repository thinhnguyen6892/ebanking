package edu.hcmus.project.ebanking.backoffice.resource.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.hcmus.project.ebanking.backoffice.model.Transaction;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionFeeType;
import edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionType;

import java.io.Serializable;
import java.time.ZonedDateTime;

import static edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionFeeType.RECEIVER;
import static edu.hcmus.project.ebanking.backoffice.model.contranst.TransactionFeeType.SENDER;

public class TransactionDto implements Serializable {

    private String id;
    private String source;
    private String target;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime createdDate;
    private String content;
    private Double amount;
    private TransactionType type;
    private TransactionFeeType feeType;
    private Double fee;

    private String amountInDetail;
    private String otpCode;

    public TransactionDto()  {

    }

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.source = transaction.getSource();
        this.target = transaction.getTarget();
        this.createdDate = transaction.getDate();
        this.content = transaction.getContent();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.feeType = transaction.getFeeType();
        this.setFee(transaction.getFee());
        switch (type) {
            case DEPOSIT:
                this.amountInDetail = "+".concat(String.valueOf(RECEIVER.equals(feeType) ? (amount - getFee()) : amount));
                break;
            default: this.amountInDetail = "-".concat(String.valueOf(SENDER.equals(feeType) ? (amount + getFee()) : amount));
        }
    }

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

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
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

    public TransactionFeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(TransactionFeeType feeType) {
        this.feeType = feeType;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getAmountInDetail() {
        return amountInDetail;
    }

    public void setAmountInDetail(String amountInDetail) {
        this.amountInDetail = amountInDetail;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
