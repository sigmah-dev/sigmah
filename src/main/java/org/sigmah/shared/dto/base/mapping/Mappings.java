package org.sigmah.shared.dto.base.mapping;

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

import java.util.HashSet;
import java.util.Set;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.base.DTO;

/**
 * Mappings utility class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Mappings {

	private Mappings() {
		// Utility class.
	}

	/**
	 * Controls the given {@code property} access regarding the given {@code currentMappingMode}.
	 * 
	 * @param property
	 *          The accessed property.
	 * @param currentMappingMode
	 *          The current mapping mode, may be {@code null}.
	 * @param dtoClass
	 *          The DTO class (used to throw exception).
	 * @throws UnavailableMappingField
	 *           If the accessed property is excluded in current mapping mode.
	 */
	public static void controlPropertyAccess(final String property, final IsMappingMode currentMappingMode, final Class<? extends DTO> dtoClass) {

		if (currentMappingMode == null) {
			// Mapping mode disabled.
			return;
		}

		final MappingField[] excludedArray = currentMappingMode.getExcludedFields();
		if (excludedArray == null) {
			// No excluded field.
			return;
		}

		// For each excluded field in the current mapping mode.
		for (final MappingField excluded : excludedArray) {

			// If the field is currently excluded, throws an exception.
			if (excluded != null && excluded.getDTOMapKey().equals(property)) {
				throw new UnavailableMappingField(dtoClass, currentMappingMode, excluded);
			}
		}
	}

	/**
	 * Returns if the given {@code current} mapping mode fulfills the necessary properties required by given
	 * {@code necessary} mapping mode.
	 * 
	 * @param current
	 *          The current mapping mode. The current object has been loaded with this mapping mode. May be {@code null}.
	 * @param necessary
	 *          The necessary mapping mode. The current object should be loaded with this mapping mode. May be
	 *          {@code null}.
	 * @return {@code true} if the given {@code current} mapping mode fulfills the necessary properties required by given
	 *         {@code necessary} mapping mode.
	 */
	public static boolean isCurrentMappingOk(final IsMappingMode current, IsMappingMode necessary) {

		if (current == null || ClientUtils.isEmpty(current.getExcludedFields())) {
			// No specific mapping, the current object contains all properties.
			return true;
		}

		if (necessary == null || ClientUtils.isEmpty(necessary.getExcludedFields())) {
			// All properties necessary, is current object already loaded with all properties?
			return current == null || ClientUtils.isEmpty(current.getExcludedFields());
		}

		if (current == necessary) {
			return true;
		}

		final Set<MappingField> necessaryExcludedFields = new HashSet<MappingField>(necessary.getExcludedFields().length);
		for (final MappingField mf : necessary.getExcludedFields()) {
			necessaryExcludedFields.add(mf);
		}

		for (final MappingField excludedField : current.getExcludedFields()) {
			if (!necessaryExcludedFields.contains(excludedField)) {
				// One of the current excluded fields is required by necessary mapping mode.
				return false;
			}
		}

		return true;
	}

}
