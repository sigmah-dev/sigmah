package org.sigmah.shared.dto;

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

import java.util.Date;
import java.util.List;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * One-to-one DTO of the {@link org.sigmah.server.domain.User} domain class.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class UserDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5865780039352557006L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "User";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String FIRST_NAME = "firstName";
	public static final String COMPLETE_NAME = "cname";
	public static final String LOCALE = "locale";
	public static final String ACTIVE = "active";

	public static final String PWD_CHANGE_KEY = "pwdChangeKey";
	public static final String PWD_CHANGE_DATE = "pwdChangeDate";
	public static final String IDD = "idd";

	// Manual mapping keys.
	public static final String ORG_UNIT = "orgUnit";
	public static final String PROFILES = "profiles";

	// Service properties keys.
	public static final String PASSWORD = "pwd";

	// Custom field names.
	private static final String CUSTOM_FIELD_ORG_UNIT = MappingField.n("orgUnitWithProfiles", "orgUnit");
	private static final String CUSTOM_FIELD_PROFILES = MappingField.n("orgUnitWithProfiles", "profiles");

	/**
	 * Mapping configurations.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Maps the user's orgUnit with {@link OrgUnitDTO.Mode#BASE} mode.
		 */
		WITH_BASE_ORG_UNIT(new CustomMappingField(CUSTOM_FIELD_ORG_UNIT, ORG_UNIT, OrgUnitDTO.Mode.BASE)),

		/**
		 * Maps the user's profile(s) with {@link ProfileDTO.Mode#BASE} mode.
		 */
		WITH_BASE_PROFILES(new CustomMappingField(CUSTOM_FIELD_PROFILES, PROFILES, ProfileDTO.Mode.BASE)),

		/**
		 * Maps the user's profile(s) with full mode.
		 */
		WITH_FULL_PROFILES(new CustomMappingField(CUSTOM_FIELD_PROFILES, PROFILES)),

		/**
		 * Maps the user's orgUnit with {@link OrgUnitDTO.Mode#BASE} mode <b>and</b> the user's profile(s) with
		 * {@link ProfileDTO.Mode#BASE} mode.
		 */
		WITH_BASE_ORG_UNIT_AND_BASE_PROFILES(new CustomMappingField(CUSTOM_FIELD_ORG_UNIT, ORG_UNIT, OrgUnitDTO.Mode.BASE), new CustomMappingField(
			CUSTOM_FIELD_PROFILES, PROFILES, ProfileDTO.Mode.BASE)),

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
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(FIRST_NAME, getFirstName());
		builder.append(COMPLETE_NAME, getCompleteName());
		builder.append(EMAIL, getEmail());
		builder.append(LOCALE, getLocale());
		builder.append(PWD_CHANGE_KEY, getChangePasswordKey());
		builder.append(PWD_CHANGE_DATE, getDateChangePasswordKeyIssued());
		builder.append(ACTIVE, getActive());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	public String getEmail() {
		return get(EMAIL);
	}

	public void setEmail(String email) {
		set(EMAIL, email);
	}

	public String getFirstName() {
		return get(FIRST_NAME);
	}

	public void setFirstName(String firstName) {
		set(FIRST_NAME, firstName);
	}

	public String getLocale() {
		return get(LOCALE);
	}

	public void setLocale(String locale) {
		set(LOCALE, locale);
	}

	public String getChangePasswordKey() {
		return get(PWD_CHANGE_KEY);
	}

	public void setChangePasswordKey(String pwdChangeKey) {
		set(PWD_CHANGE_KEY, pwdChangeKey);
	}

	public Date getDateChangePasswordKeyIssued() {
		return get(PWD_CHANGE_DATE);
	}

	public void setDateChangePasswordKeyIssued(Date pwdChangeDate) {
		set(PWD_CHANGE_DATE, pwdChangeDate);
	}

	public String getCompleteName() {
		return get(COMPLETE_NAME);
	}

	public void setCompleteName(String cname) {
		set(COMPLETE_NAME, cname);
	}

	public void setIdd(int id) {
		set(IDD, id);
	}

	public int getIdd() {
		return (Integer) get(IDD);
	}

	public void setActive(boolean active) {
		set(ACTIVE, active);
	}

	public boolean getActive() {
		return ClientUtils.isTrue(get(ACTIVE));
	}

	public OrgUnitDTO getOrgUnit() {
		return get(ORG_UNIT);
	}

	public void setOrgUnit(OrgUnitDTO orgUnit) {
		set(ORG_UNIT, orgUnit);
	}

	public List<ProfileDTO> getProfiles() {
		return get(PROFILES);
	}

	public void setProfiles(List<ProfileDTO> profiles) {
		set(PROFILES, profiles);
	}

}
