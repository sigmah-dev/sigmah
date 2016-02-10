package org.sigmah.shared.dto.profile;

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

import java.util.Map;
import java.util.Set;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 * DTO mapping class for entity profile.Profile.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProfileDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4319548689359747450L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "profile.Profile";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String GLOBAL_PERMISSIONS = "globalPermissions";
	public static final String PRIVACY_GROUPS = "privacyGroups";
	
	// Service key.
	public static final String PROFILE = "profile";

	/**
	 * Mapping configurations.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Base mapping retrieving only profile base data (only id and name, no related DTO).
		 */
		BASE(new MappingField(GLOBAL_PERMISSIONS), new MappingField(PRIVACY_GROUPS)),

		/**
		 * In addition to base data, this mapping includes:
		 * <ul>
		 * <li>{@link ProfileDTO#GLOBAL_PERMISSIONS}</li>
		 * </ul>
		 */
		WITH_GLOBAL_PERMISSIONS(new MappingField(PRIVACY_GROUPS)),

		/**
		 * In addition to base data, this mapping includes:
		 * <ul>
		 * <li>{@link ProfileDTO#PRIVACY_GROUPS}</li>
		 * </ul>
		 */
		WITH_PRIVACY_GROUPS(new MappingField(GLOBAL_PERMISSIONS)),

		;

		private final CustomMappingField[] customFields;
		private final MappingField[] excludedFields;

		private Mode(final MappingField... excludedFields) {
			this(null, excludedFields);
		}
		
		private Mode(final CustomMappingField... customFields) {
			this(customFields, (MappingField[]) null);
		}

		private Mode(final CustomMappingField[] customFields, final MappingField... excludedFields) {
			this.customFields = customFields;
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
		public CustomMappingField[] getCustomFields() {
			return customFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MappingField[] getExcludedFields() {
			return excludedFields;
		}
	}

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
		builder.append(NAME, getName());
	}

	// Name.
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Global permissions.
	public Set<GlobalPermissionEnum> getGlobalPermissions() {
		return get(GLOBAL_PERMISSIONS);
	}

	public void setGlobalPermissions(Set<GlobalPermissionEnum> globalPermissions) {
		set(GLOBAL_PERMISSIONS, globalPermissions);
	}

	// Privacy groups.
	public Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> getPrivacyGroups() {
		return get(PRIVACY_GROUPS);
	}

	public void setPrivacyGroups(Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroups) {
		set(PRIVACY_GROUPS, privacyGroups);
	}

}
