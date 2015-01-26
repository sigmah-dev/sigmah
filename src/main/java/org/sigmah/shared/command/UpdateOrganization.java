package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.organization.OrganizationDTO;

/**
 * <p>
 * Update organization command.
 * </p>
 * <p>
 * Updates organization's name and logo properties.
 * </p>
 * 
 * @author Aurélien Ponçon
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateOrganization extends AbstractCommand<OrganizationDTO> {

	private OrganizationDTO organization;

	public UpdateOrganization() {
		// Serialization.
	}

	/**
	 * Initializes a new command.
	 * 
	 * @param organization
	 *          The {@link OrganizationDTO} (id, name and logo filename should be set).
	 */
	public UpdateOrganization(final OrganizationDTO organization) {
		this.organization = organization;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("organization", organization);
	}

	public OrganizationDTO getOrganization() {
		return organization;
	}
}
