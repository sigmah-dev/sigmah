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

public class FrameworkElementImplementationDTO extends AbstractModelDataEntityDTO<Integer> {
	private static final long serialVersionUID = 2258051061935224240L;

	public static final String ENTITY_NAME = "FrameworkElementImplementation";

	public static final String ID = "id";
	public static final String FRAMEWORK_ELEMENT_ID = "frameworkElementId";
	public static final String FLEXIBLE_ELEMENT_ID = "flexibleElementId";

	@Override
	public Integer getId() {
		return get(ID);
	}

	@Override
	public void setId(Integer id) {
		set(ID, id);
	}

	public Integer getFrameworkElementId() {
		return get(FRAMEWORK_ELEMENT_ID);
	}

	public void setFrameworkElementId(Integer frameworkElementId) {
		set(FRAMEWORK_ELEMENT_ID, frameworkElementId);
	}

	public Integer getFlexibleElementId() {
		return get(FLEXIBLE_ELEMENT_ID);
	}

	public void setFlexibleElementId(Integer flexibleElementId) {
		set(FLEXIBLE_ELEMENT_ID, flexibleElementId);
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
}
