package edu.hcmus.project.ebanking.ws.resource.dto;

import edu.hcmus.project.ebanking.ws.model.Transaction;
import edu.hcmus.project.ebanking.ws.model.TransactionFeeType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TransactionDto implements Serializable {
    private String transactionRef;
    private ZonedDateTime createdDate;
    private String content;
    private TransactionFeeType feeType;
    private Double fee;

    public TransactionDto(Transaction transaction) {
        this.transactionRef = transaction.getId();
        this.createdDate = transaction.getDate();
        this.content = transaction.getContent();
        this.feeType = transaction.getFeeType();
        this.fee = transaction.getFee();
    }
}
