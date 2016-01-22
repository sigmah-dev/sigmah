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
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

/**
 * TripletValueDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class TripletValueDTO extends AbstractModelDataEntityDTO<Integer> implements ListableValue {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.TripletValue";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String CODE = "code";
	public static final String PERIOD = "period";
	public static final String TYPE = "type";
	public static final String INDEX = "index";

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
		builder.append(NAME, getName());
		builder.append(CODE, getCode());
		builder.append(PERIOD, getPeriod());
		builder.append(TYPE, getType());
		builder.append(INDEX, getIndex());
	}

	// Triplet value index
	public int getIndex() {
		final Object index = get(INDEX);
		return index != null ? (Integer) index : -1;
	}

	public void setIndex(int index) {
		set(INDEX, index);
	}

	// Triplet value code
	public String getCode() {
		return get(CODE);
	}

	public void setCode(String code) {
		set(CODE, code);
	}

	// Triplet value name
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Triplet value period
	public String getPeriod() {
		return get(PERIOD);
	}

	public void setPeriod(String period) {
		set(PERIOD, period);
	}

	// Chnage type for history.
	public ValueEventChangeType getType() {
		return get(TYPE);
	}

	public void setType(ValueEventChangeType type) {
		set(TYPE, type);
	}
}
