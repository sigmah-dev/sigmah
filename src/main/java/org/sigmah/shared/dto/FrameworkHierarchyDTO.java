package org.sigmah.shared.dto;

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
