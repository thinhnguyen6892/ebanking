package edu.hcmus.project.ebanking.backoffice.resource.debt.dto;

import edu.hcmus.project.ebanking.data.model.Debt;
import edu.hcmus.project.ebanking.backoffice.resource.transaction.dto.TransactionDto;

public class DebtPaymentDto extends DebtDto {

    public DebtPaymentDto(Debt debt) {
        super(debt);
    }

    private TransactionDto transactionInfo;

    public TransactionDto getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(TransactionDto transactionInfo) {
        this.transactionInfo = transactionInfo;
    }
}
