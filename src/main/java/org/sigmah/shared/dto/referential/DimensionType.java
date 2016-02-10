package org.sigmah.shared.dto.referential;

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

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.result.Result;

/**
 * Dimension types enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum DimensionType implements Result {

	Partner(SortType.NATURAL_LABEL),
	Activity(SortType.DEFINED),
	ActivityCategory(SortType.DEFINED),
	Database(SortType.NATURAL_LABEL),
	AdminLevel(SortType.NATURAL_LABEL),
	Date(SortType.NATURAL_VALUE),
	Status(SortType.DEFINED),
	Indicator(SortType.DEFINED),
	IndicatorCategory(SortType.DEFINED),
	AttributeGroup(SortType.NATURAL_VALUE),
	Site(SortType.NATURAL_LABEL);

	private final SortType sortOrder;

	private DimensionType(SortType sortOrder) {
		this.sortOrder = sortOrder;
	}

	public SortType getSortOrder() {
		return this.sortOrder;
	}

	/**
	 * Returns the given {@code name} corresponding {@code DimensionType} value.
	 * 
	 * @param name
	 *          The {@code DimensionType} value name (case insensitive).
	 * @return The given {@code name} corresponding {@code DimensionType} value.
	 * @throws RuntimeException
	 *           If the given {@code name} does not correspond to any {@code DimensionType}.
	 */
	public static DimensionType fromString(final String name) {

		if (ClientUtils.isNotBlank(name)) {

			final String nameLowered = name.toLowerCase();

			for (final DimensionType type : values()) {
				if (type.toString().toLowerCase().equals(nameLowered)) {
					return type;
				}
			}
		}

		throw new RuntimeException("No DimensionType has been found for name '" + name + "'.");
	}

}
