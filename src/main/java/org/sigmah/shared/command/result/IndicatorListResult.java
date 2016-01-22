package org.sigmah.shared.command.result;

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

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;

/**
 * IndicatorListResult.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IndicatorListResult extends ListResult<IndicatorDTO> {

	private List<IndicatorGroup> groups = new ArrayList<IndicatorGroup>();
	private List<IndicatorDTO> ungroupedIndicators = new ArrayList<IndicatorDTO>();

	public IndicatorListResult() {
		super(); // Serialization.
	}

	public IndicatorListResult(List<IndicatorDTO> data) {
		super(data);
	}

	public List<IndicatorGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<IndicatorGroup> groups) {
		this.groups = groups;
	}

	public List<IndicatorDTO> getUngroupedIndicators() {
		return ungroupedIndicators;
	}

	public void setUngroupedIndicators(List<IndicatorDTO> ungroupedIndicators) {
		this.ungroupedIndicators = ungroupedIndicators;
	}

	public IndicatorDTO getIndicatorById(int id) {
		for (final IndicatorDTO indicator : getList()) {
			if (indicator.getId().equals(id)) {
				return indicator;
			}
		}
		throw new IllegalArgumentException();
	}

}
