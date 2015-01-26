package org.sigmah.shared.dto.organization;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * DTO mapping class for entity Organization.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class OrganizationDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "Organization";

	// Map keys.
	private static final String ROOT = "root";
	private static final String NAME = "name";
	private static final String LOGO = "logo";

	/**
	 * Mapping configurations.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Basic mapping without org units tree.
		 */
		BASE(new MappingField("root", ROOT)),

		/**
		 * Mapping with org units tree.
		 */
		WITH_ROOT;

		private final MappingField[] excludedFields;

		private Mode(MappingField... excludedFields) {
			this.excludedFields = excludedFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMapId() {
			return name();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MappingField[] getExcludedFields() {
			return excludedFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CustomMappingField[] getCustomFields() {
			return null;
		}
	}

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8285349034203126628L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(LOGO, getLogo());
		builder.append(ROOT, getRoot());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	// Organization name.
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Organization logo path.
	public String getLogo() {
		return get(LOGO);
	}

	public void setLogo(String logo) {
		set(LOGO, logo);
	}

	// Root org unit
	public OrgUnitDTO getRoot() {
		return get(ROOT);
	}

	public void setRoot(OrgUnitDTO root) {
		set(ROOT, root);
	}

}
