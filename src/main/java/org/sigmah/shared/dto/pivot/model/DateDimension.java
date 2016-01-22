package org.sigmah.shared.dto.pivot.model;

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

import org.sigmah.shared.dto.referential.DimensionType;

/**
 * Models a data dimension that can be grouped by week, month, quarter, year, etc
 * 
 * @author Alex Bertram (v1.3)
 */
public class DateDimension extends Dimension {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6432630999356593410L;

	private DateUnit unit;
	private String format;

	public DateDimension() {
	}

	public DateDimension(String name, int id, DateUnit unit, String format) {
		super(DimensionType.Date);
		this.unit = unit;
		this.format = format;
		set("caption", name);
		set("id", "dim_date_" + id);
	}

	public DateDimension(DateUnit unit) {
		super(DimensionType.Date);
		this.unit = unit;
	}

	public String getId() {
		return get("id");
	}

	public DateUnit getUnit() {
		return unit;
	}

	public void setUnit(DateUnit unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof DateDimension)) {
			return false;
		}

		DateDimension that = (DateDimension) other;
		return this.unit == that.unit;
	}

	/**
	 * @return The format string that should be applied to category values in this dimension. See
	 *         {@link java.text.SimpleDateFormat#applyPattern(String)} for details on the grammar of this format string.
	 */
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
