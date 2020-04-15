package edu.hcmus.project.ebanking.ws.resource.dto;

import edu.hcmus.project.ebanking.ws.model.Transaction;
import edu.hcmus.project.ebanking.ws.model.TransactionFeeType;
import edu.hcmus.project.ebanking.ws.model.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TransactionDto extends BaseRequestDto {
    private String transactionRef;
    private ZonedDateTime createdDate;
    private String content;
    private TransactionFeeType feeType;
    private Double fee;
    private TransactionStatus status;
    private String receiveAccount;

    public TransactionDto(Transaction transaction) {
        this.transactionRef = transaction.getId();
        this.createdDate = transaction.getDate();
        this.content = transaction.getContent();
        this.feeType = transaction.getFeeType();
        this.fee = transaction.getFee();
        this.status = transaction.getStatus();
        this.receiveAccount = transaction.getSource();
    }

    public TransactionDto clone() throws CloneNotSupportedException {
        return (TransactionDto) super.clone();
    }
}
