package org.sigmah.shared.command;

import org.sigmah.shared.command.result.OrganizationResult;
import org.sigmah.shared.dto.OrganizationDTO;

/**
 * Update organization command.
 * 
 * @author Aurélien Ponçon
 */
public class UpdateOrganization implements Command<OrganizationResult> {

    private static final long serialVersionUID = 8702867672468516230L;

    private OrganizationDTO organization;
    private String newName;

    public UpdateOrganization() {
    }

    public UpdateOrganization(OrganizationDTO organization, String newName) {
        this.organization = organization;
        this.newName = newName;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public String getNewName() {
        return newName;
    }
}
