package org.sigmah.shared.dto.pivot.content;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.dto.referential.DimensionType;

/**
 * Encapsulates a text description of a given filter restriction.
 */
public class FilterDescription implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6616583645894028758L;

	private DimensionType type;
	private List<String> labels;

	/**
	 * Required for GWT serialization
	 */
	protected FilterDescription() {
		// Serialization.
	}

	public FilterDescription(DimensionType type, List<String> labels) {
		this.type = type;
		this.labels = labels;
	}

	public FilterDescription(DimensionType type, String label) {
		this.type = type;
		this.labels = new ArrayList<String>(1);
		this.labels.add(label);
	}

	public DimensionType getDimensionType() {
		return type;
	}

	/**
	 * @return The labels of the dimension categories specified in this restriction.
	 */
	public List<String> getLabels() {
		return labels;
	}

	public String joinLabels(String delimeter) {
		final StringBuilder sb = new StringBuilder();
		for (final String label : labels) {
			if (sb.length() != 0) {
				sb.append(delimeter);
			}
			sb.append(label);
		}
		return sb.toString();
	}

}
