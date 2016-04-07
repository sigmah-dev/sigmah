package org.sigmah.shared.dto;

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
