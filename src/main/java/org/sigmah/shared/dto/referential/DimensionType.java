package org.sigmah.shared.dto.referential;

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
