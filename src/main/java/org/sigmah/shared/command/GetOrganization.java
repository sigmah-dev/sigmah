package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.organization.OrganizationDTO;

/**
 * Retrieves an organization with the given id.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetOrganization extends AbstractCommand<OrganizationDTO> {

	/**
	 * Mapping mode.
	 */
	private OrganizationDTO.Mode mode;

	/**
	 * Organization id.
	 */
	private int id;

	public GetOrganization() {
		// Serialization.
	}

	public GetOrganization(OrganizationDTO.Mode mode, int id) {
		this.mode = mode;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OrganizationDTO.Mode getMode() {
		return mode;
	}

	public void setMode(OrganizationDTO.Mode mode) {
		this.mode = mode;
	}

}
