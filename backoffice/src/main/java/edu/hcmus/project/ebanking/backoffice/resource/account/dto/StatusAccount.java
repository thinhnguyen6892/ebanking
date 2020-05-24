package edu.hcmus.project.ebanking.backoffice.resource.account.dto;

import java.io.Serializable;

public class StatusAccount implements Serializable {
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
