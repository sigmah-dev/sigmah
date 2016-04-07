package org.sigmah.shared.dto;

import java.util.List;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

public class FrameworkDTO extends AbstractModelDataEntityDTO<Integer> {
	private static final long serialVersionUID = 5145792301859752203L;

	public static final String ENTITY_NAME = "Framework";

	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String FRAMEWORK_HIERARCHIES = "frameworkHierarchies";

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

	public List<FrameworkHierarchyDTO> getFrameworkHierarchies() {
		return get(FRAMEWORK_HIERARCHIES);
	}

	public void setFrameworkHierarchies(List<FrameworkHierarchyDTO> frameworkHierarchies) {
		set(FRAMEWORK_HIERARCHIES, frameworkHierarchies);
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
}
