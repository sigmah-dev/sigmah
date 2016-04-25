package org.sigmah.shared.dto;

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

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

public class FrameworkElementDTO extends AbstractModelDataEntityDTO<Integer> {
	private static final long serialVersionUID = 5145792301859752203L;

	public static final String ENTITY_NAME = "Framework";

	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String DATA_TYPE = "dataType";

	@Override
	public Integer getId() {
		return get(ID);
	}

	@Override
	public void setId(Integer id) {
		set(ID, id);
	}

	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		set(LABEL, label);
	}

	public ElementTypeEnum getDataType() {
		return get(DATA_TYPE);
	}

	public void setDataType(ElementTypeEnum dataType) {
		set(DATA_TYPE, dataType);
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
}
