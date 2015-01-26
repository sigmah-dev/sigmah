package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * Light DTO mapping class for entity ProjectFunding.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectFundingDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Linked project type.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum LinkedProjectType {

		/**
		 * Type of project that is <b>funding</b> another project.
		 */
		FUNDING_PROJECT(FUNDING_ID),

		/**
		 * Type of project that is <b>funded by</b> another project.
		 */
		FUNDED_PROJECT(FUNDED_ID);

		private final String propertyIdKey;

		private LinkedProjectType(final String propertyIdKey) {
			this.propertyIdKey = propertyIdKey;
		}

		public String getPropertyIdKey() {
			return propertyIdKey;
		}

		/**
		 * Returns the given {@code value} corresponding {@link LinkedProjectType}.
		 * 
		 * @param value
		 *          The value (case insensitive).
		 * @return The given {@code value} corresponding {@link LinkedProjectType}, or {@code null}.
		 */
		public static LinkedProjectType fromString(final String value) {
			try {

				return LinkedProjectType.valueOf(value.toUpperCase());

			} catch (final Exception e) {
				return null;
			}
		}
	}

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -191315535238325514L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "ProjectFunding";

	// DTO attributes keys.
	public static final String PERCENTAGE = "percentage";
	public static final String FUNDING = "funding";
	public static final String FUNDED = "funded";

	// Sub projects ids (used by service).
	public static final String FUNDING_ID = "fundingId";
	public static final String FUNDED_ID = "fundedId";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(PERCENTAGE, getPercentage());
	}

	// Funding projects
	public ProjectDTO getFunding() {
		return get(FUNDING);
	}

	public void setFunding(ProjectDTO funding) {
		set(FUNDING, funding);
	}

	// Funded projects.
	public ProjectDTO getFunded() {
		return get(FUNDED);
	}

	public void setFunded(ProjectDTO funded) {
		set(FUNDED, funded);
	}

	// Funding percentage.
	public Double getPercentage() {
		return (Double) get(PERCENTAGE);
	}

	public void setPercentage(Double percentage) {
		set(PERCENTAGE, percentage);
	}

}
