package edu.hcmus.project.ebanking.backoffice.resource.user.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class ChangePasswordDto implements Serializable {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
