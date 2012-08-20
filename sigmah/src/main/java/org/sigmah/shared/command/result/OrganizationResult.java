package org.sigmah.shared.command.result;

import org.sigmah.shared.dto.OrganizationDTO;

/**
 * An organization
 * 
 * @author Aurélien Ponçon
 * 
 */
public class OrganizationResult implements CommandResult {

    private static final long serialVersionUID = -9218472628691958050L;
    
    private OrganizationDTO organization;

    
    public OrganizationResult() {}
    
    public OrganizationResult(OrganizationDTO organization) {
        this.organization = organization;
    }
    
    public OrganizationDTO getOrganization() {
        return organization;
    }

    
    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }
    
}
