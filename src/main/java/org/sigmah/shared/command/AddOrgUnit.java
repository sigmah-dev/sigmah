package org.sigmah.shared.command;

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
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * AddOrgUnit command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class AddOrgUnit extends AbstractCommand<CreateResult> {

	private Integer parentId;
	private Integer modelId;
	private String calendarName;
	private OrgUnitDTO unit;
	private OrgUnitDTO.Mode mappingMode;

	protected AddOrgUnit() {
		// Serialization.
	}

	public AddOrgUnit(Integer parentId, Integer modelId, String calendarName, OrgUnitDTO unit, OrgUnitDTO.Mode mappingMode) {
		this.parentId = parentId;
		this.modelId = modelId;
		this.calendarName = calendarName;
		this.unit = unit;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("parentId", parentId);
		builder.append("modelId", modelId);
		builder.append("calendarName", calendarName);
		builder.append("unit", unit);
		builder.append("mappingMode", mappingMode);
	}

	public Integer getParentId() {
		return parentId;
	}

	public OrgUnitDTO getUnit() {
		return unit;
	}

	public Integer getModelId() {
		return modelId;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public OrgUnitDTO.Mode getMappingMode() {
		return mappingMode;
	}
}
