package org.sigmah.shared.dto.country;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.LocationTypeDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;

/**
 * One-to-one DTO for {@link org.sigmah.server.domain.Country Country} domain objects.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class CountryDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4620267851446986278L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "Country";

	// Attribute keys.
	public static final String NAME = "name";
	public static final String COMPLETE_NAME = "completeName";
	public static final String CODE_ISO = "codeISO";
	public static final String BOUNDS = "bounds";

	public static final String ADMIN_LEVELS = "adminLevels";
	public static final String LOCATION_TYPES = "locationTypes";

	/**
	 * Mapping configurations.
	 * 
	 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Excludes related DTOs (admin levels, location types).
		 */
		BASE(new MappingField(ADMIN_LEVELS), new MappingField(LOCATION_TYPES)),

		;

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
	 * Returns the given {@code country} corresponding readable value with following format:
	 * 
	 * <pre>
	 * <em>country_name</em> (<em>country_ISO_code</em>)
	 * </pre>
	 * 
	 * @param country
	 *          The {@link CountryDTO} instance.
	 * @return The given {@code country} corresponding readable value, or empty string if {@code null}.
	 */
	public static final String toString(final CountryDTO country) {

		return country == null ? "" : country.getCompleteName();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(CODE_ISO, getCodeISO());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	// Name.
	public String getName() {
		return get(NAME);
	}

	public void setName(String value) {
		set(NAME, value);
		generateCompleteName();
	}

	// ISO code.
	public String getCodeISO() {
		return get(CODE_ISO);
	}

	public void setCodeISO(String codeISO) {
		set(CODE_ISO, codeISO);
		generateCompleteName();
	}

	// Complete name.
	public String getCompleteName() {
		return get(COMPLETE_NAME);
	}

	public void setCompleteName(String completeName) {
		set(COMPLETE_NAME, completeName);
	}

	// Bounding box.
	public BoundingBoxDTO getBounds() {
		return get(BOUNDS);
	}

	public void setBounds(BoundingBoxDTO bounds) {
		set(BOUNDS, bounds);
	}

	// Admin levels.
	public List<AdminLevelDTO> getAdminLevels() {
		return get(ADMIN_LEVELS);
	}

	public void setAdminLevels(List<AdminLevelDTO> levels) {
		set(ADMIN_LEVELS, levels);
	}

	// Location types.
	public List<LocationTypeDTO> getLocationTypes() {
		return get(LOCATION_TYPES);
	}

	public void setLocationTypes(List<LocationTypeDTO> locationTypes) {
		set(LOCATION_TYPES, locationTypes);
	}

	// ---------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------

	/**
	 * Generates the country complete name.<br>
	 * <em>This method should be executed each time the country's name or ISO code is updated.</em>
	 */
	private void generateCompleteName() {
		setCompleteName(getName() + " (" + getCodeISO() + ')');
	}

	/**
	 * Finds an AdminEntity by id
	 *
	 * @param levelId
	 *          the id of the AdminEntity to return
	 * @return the AdminEntity with corresponding id or null if no such AdminEntity is found in the list
	 */
	public AdminLevelDTO getAdminLevelById(int levelId) {
		for (final AdminLevelDTO level : getAdminLevels()) {
			if (level.getId().equals(levelId)) {
				return level;
			}
		}
		return null;
	}

	/**
	 * Returns a list of <code>AdminLevelDTO</code>s that are ancestors of the the AdminLevel with an id of
	 * <code>levelId</code> in order descending from the root.
	 *
	 * @param levelId
	 *          the id of AdminLevel
	 * @return a list of AdminLevelDTOs in <code>adminLevels</code> which are ancestors of the AdminLevel with the id of
	 *         <code>levelId</code>, or null if no AdminLevelDTO with the given id or exists or if the indicated
	 *         AdminLevel is a root level.
	 */
	public List<AdminLevelDTO> getAdminLevelAncestors(int levelId) {

		final List<AdminLevelDTO> ancestors = new ArrayList<AdminLevelDTO>();

		AdminLevelDTO level = getAdminLevelById(levelId);

		if (level == null) {
			return null;
		}

		while (true) {
			ancestors.add(0, level);

			if (level.isRoot()) {
				return ancestors;

			} else {
				level = getAdminLevelById(level.getParentLevelId());
			}
		}
	}

	/**
	 * Returns the <code>LocationTypeDTO</code> with the given <code>locationTypeId</code>
	 * 
	 * @param locationTypeId
	 *          the id of a <code>LocationTypeDTO</code> in <code>locationTypes</code>
	 * @return the <code>LocationTypeDTO</code> in <code>locationTypes</code> with the id <code>locationTypeId</code>
	 */
	public LocationTypeDTO getLocationTypeById(int locationTypeId) {
		for (final LocationTypeDTO type : getLocationTypes()) {
			if (type.getId().equals(locationTypeId)) {
				return type;
			}
		}
		return null;
	}

}
