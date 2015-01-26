package org.sigmah.shared.command;

import org.sigmah.shared.dto.OrganizationDTO;

/**
 * Retrieves an organization with the given id.
 * 
 * @author tmi
 * 
 */
public class GetOrganization implements Command<OrganizationDTO> {

    private static final long serialVersionUID = 5675515456984800856L;

    private int id;

    public GetOrganization() {
        // required, or serialization exception
    }

    public GetOrganization(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GetOrganization other = (GetOrganization) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
