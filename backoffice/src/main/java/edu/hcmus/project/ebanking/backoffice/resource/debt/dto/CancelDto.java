package edu.hcmus.project.ebanking.backoffice.resource.debt.dto;

import edu.hcmus.project.ebanking.backoffice.model.contranst.DebtStatus;

import java.io.Serializable;

public class CancelDto implements Serializable {
    private DebtStatus status;
    private String note;

    public DebtStatus getStatus() {
        return status;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
