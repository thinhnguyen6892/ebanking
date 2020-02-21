package edu.hcmus.project.ebanking.backoffice.resource.role;

import java.io.Serializable;

public class RoleDto implements Serializable {
    private String roleId;
    private String name;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
