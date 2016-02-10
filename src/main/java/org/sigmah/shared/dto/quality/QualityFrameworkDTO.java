package org.sigmah.shared.dto.quality;

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

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.allen_sauer.gwt.log.client.Log;

/**
 * DTO mapping class for entity quality.QualityFramework.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class QualityFrameworkDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1494859762914765504L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "quality.QualityFramework";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("types", getTypes());
		builder.append("criteria", getCriteria());
	}

	/**
	 * Retrieves the criterion type with the given level.
	 * 
	 * @param level
	 *          The level.
	 * @return The criterion type.
	 */
	public CriterionTypeDTO getType(int level) {

		Log.debug(toString());

		if (level < 0) {
			return null;
		}

		if (getTypes() == null) {
			return null;
		}

		for (final CriterionTypeDTO type : getTypes()) {
			if (type.getLevel() == level) {
				return type;
			}
		}

		return null;
	}

	// Framework id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Framework label.
	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	// Criteria.
	public List<QualityCriterionDTO> getCriteria() {
		return get("criteria");
	}

	public void setCriteria(List<QualityCriterionDTO> criteria) {
		set("criteria", criteria);
	}

	// Types.
	public List<CriterionTypeDTO> getTypes() {
		return get("types");
	}

	public void setTypes(List<CriterionTypeDTO> types) {
		set("types", types);
	}

}
