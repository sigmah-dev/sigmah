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

public class FrameworkFulfillmentDTO extends AbstractModelDataEntityDTO<Integer> {
	private static final long serialVersionUID = -8816158766960817299L;

	public static final String ENTITY_NAME = "FrameworkFulfillment";

	public static final String ID = "id";
	public static final String FRAMEWORK_ID = "frameworkId";
	public static final String PROJECT_MODEL_ID = "projectModelId";
	public static final String FRAMEWORK_ELEMENT_IMPLEMENTATIONS = "frameworkElementImplementations";
	public static final String REJECT_REASON = "rejectReason";

	@Override
	public Integer getId() {
		return get(ID);
	}

	@Override
	public void setId(Integer id) {
		set(ID, id);
	}

	public Integer getFrameworkId() {
		return get(FRAMEWORK_ID);
	}

	public void setFrameworkId(Integer frameworkId) {
		set(FRAMEWORK_ID, frameworkId);
	}

	public Integer getProjectModelId() {
		return get(PROJECT_MODEL_ID);
	}

	public void setProjectModelId(Integer projectModelId) {
		set(PROJECT_MODEL_ID, projectModelId);
	}

	public List<FrameworkElementImplementationDTO> getFrameworkElementImplementations() {
		return get(FRAMEWORK_ELEMENT_IMPLEMENTATIONS);
	}

	public void setFrameworkElementImplementations(List<FrameworkElementImplementationDTO> frameworkElementImplementations) {
		set(FRAMEWORK_ELEMENT_IMPLEMENTATIONS, frameworkElementImplementations);
	}

	public String getRejectReason() {
		return get(REJECT_REASON);
	}

	public void setRejectReason(String rejectReason) {
		set(REJECT_REASON, rejectReason);
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
}
