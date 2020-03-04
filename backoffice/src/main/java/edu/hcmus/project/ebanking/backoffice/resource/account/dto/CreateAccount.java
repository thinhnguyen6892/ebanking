package edu.hcmus.project.ebanking.backoffice.resource.account.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "Create SAVING account")
public class CreateAccount implements Serializable {

    @ApiModelProperty(notes = "The account balance")
    private Double balance;
    private Date expired;
    private Boolean status = Boolean.TRUE;

    @ApiModelProperty(notes = "Owner")
    private Long ownerId;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
