package org.sigmah.shared.dto;

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
