package org.sigmah.shared.dto.organization;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
