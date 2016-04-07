package org.sigmah.shared.dto;

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
