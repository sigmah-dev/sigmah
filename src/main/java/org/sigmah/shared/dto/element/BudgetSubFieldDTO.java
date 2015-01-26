package org.sigmah.shared.dto.element;

import org.sigmah.shared.domain.element.BudgetSubFieldType;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class BudgetSubFieldDTO extends BaseModelData implements EntityDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2072338520863126538L;

	@Override
	public int getId() {
		if (get("id") != null) {
			return (Integer) get("id");
		} else {
			return -1;
		}
	}

	public void setId(int id) {
		set("id", id);
	}
	
	@Override
	public String getEntityName() {
		// Gets the entity name mapped by the current DTO starting from the
		// "server.domain" package name.
		return "element.BudgetField";
	}

	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	public BudgetElementDTO getBudgetElementDTO() {
		return get("budgetElementDTO");
	}

	public void setBudgetElementDTO(BudgetElementDTO budgetElementDTO) {
		set("budgetElementDTO", budgetElementDTO);
	}

	public Integer getFieldOrder() {
		return get("fieldOrder");
	}

	public void setFieldOrder(Integer order) {
		set("fieldOrder", order);
	}

	public BudgetSubFieldType getType() {
		return get("type");
	}

	public void setType(BudgetSubFieldType type) {
		set("type", type);
	}
	

}
