package edu.hcmus.project.ebanking.ws.resource;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class AccountRequest {
    @NotNull
    private ZonedDateTime submittedDate;
    @NotNull
    private Long validity;

    @NotNull
    private String acd;

    @NotNull
    private String hash;


    public ZonedDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(ZonedDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public Long getValidity() {
        return validity;
    }

    public void setValidity(Long validity) {
        this.validity = validity;
    }

    public String getAcd() {
        return acd;
    }

    public void setAcd(String acd) {
        this.acd = acd;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
