package edu.hcmus.project.ebanking.backoffice.resource.debt.dto;

import edu.hcmus.project.ebanking.data.model.Account;
import edu.hcmus.project.ebanking.data.model.Debt;
import edu.hcmus.project.ebanking.data.model.User;
import edu.hcmus.project.ebanking.data.model.contranst.DebtStatus;
import edu.hcmus.project.ebanking.backoffice.resource.user.dto.UserDto;

import javax.jws.soap.SOAPBinding;
import java.io.Serializable;
import java.util.Date;


public class DebtDto implements Serializable {
    private int id;
    private Date createDate;
    private DebtStatus status;
    private Long holder;
    private UserDto userHolder;
    private String debtor;
    private UserDto userDebtor;
    private String content;
    private Double amount;

    public DebtDto() {

    }

    public DebtDto(Debt debt) {
        this.createDate = debt.getCreateDate();
        this.status = debt.getStatus();
        User holder =  debt.getHolder();
        Account debtor = debt.getDebtor();
        this.content = debt.getContent();
        this.amount = debt.getAmount();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public DebtStatus getStatus() {
        return status;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }

    public Long getHolder() {
        return holder;
    }

    public void setHolder(Long holder) {
        this.holder = holder;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtor) {
        this.debtor = debtor;
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

    public UserDto getUserDebtor() {
        return userDebtor;
    }

    public void setUserDebtor(UserDto userDebtor) {
        this.userDebtor = userDebtor;
    }

    public UserDto getUserHolder() {
        return userHolder;
    }

    public void setUserHolder(UserDto userHolder) {
        this.userHolder = userHolder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
