package org.sigmah.shared.dto.value;

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
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * IndicatorsListValueDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IndicatorsListValueDTO extends AbstractModelDataEntityDTO<Integer> implements ListableValue {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.IndicatorsListValue";

	// DTO attributes keys.
	public static final String ID_LIST = "idList";
	public static final String INDICATOR = "indicator";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(ID_LIST, getIdList());
	}

	// Indicators list value list id
	public int getIdList() {
		return (Integer) get(ID_LIST);
	}

	public void setIdList(int idList) {
		set(ID_LIST, idList);
	}

	// Indicator's reference
	public IndicatorDTO getIndicatorDTO() {
		return (IndicatorDTO) get(INDICATOR);
	}

	public void setIndicatorDTO(IndicatorDTO indicator) {
		set(INDICATOR, indicator);
	}

}
