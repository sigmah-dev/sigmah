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

import java.util.List;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

public class FrameworkHierarchyDTO extends AbstractModelDataEntityDTO<Integer> {
	private static final long serialVersionUID = 2926098762140134673L;

	public String ENTITY_NAME = "FrameworkHierarchy";

	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String FRAMEWORK_ELEMENTS = "frameworkElements";

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

	public List<FrameworkElementDTO> getFrameworkElements() {
		return get(FRAMEWORK_ELEMENTS);
	}

	public void setFrameworkElements(List<FrameworkElementDTO> frameworkElements) {
		set(FRAMEWORK_ELEMENTS, frameworkElements);
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
}
