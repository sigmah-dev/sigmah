package org.sigmah.shared.command;

import org.sigmah.shared.command.result.UserListResult;

public class GetUsersByOrganization implements Command<UserListResult> {

    private static final long serialVersionUID = 7100250221434383156L;

    public int organizationId;

    public Integer userId;

    public GetUsersByOrganization() {
        // serialization.
    }

    public GetUsersByOrganization(int organizationId) {
        this.organizationId = organizationId;
    }

    public GetUsersByOrganization(int organizationId, Integer userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
