package org.sigmah.shared.dto.importation;

import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VariableBudgetSubFieldDTO extends BaseModelData implements EntityDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -171871913478215607L;

	@Override
	public int getId() {
		if (get("id") != null)
			return (Integer) get("id");
		else
			return -1;
	}

	public void setId(int id) {
		// TODO Auto-generated method stub
		set("id", id);
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return "importation.VariableBudgetSubField";
	}

	/**
	 * @return the variableDTO
	 */
	public VariableDTO getVariableDTO() {
		return get("variableDTO");
	}

	/**
	 * @param variableDTO
	 *            the variableDTO to set
	 */
	public void setVariableDTO(VariableDTO variableDTO) {
		set("variableDTO", variableDTO);
	}

	/**
	 * @return the budgetSubFieldDTO
	 */
	public BudgetSubFieldDTO getBudgetSubFieldDTO() {
		return get("budgetSubFieldDTO");
	}

	/**
	 * @param budgetSubFieldDTO
	 *            the budgetSubFieldDTO to set
	 */
	public void setBudgetSubFieldDTO(BudgetSubFieldDTO budgetSubFieldDTO) {
		set("budgetSubFieldDTO", budgetSubFieldDTO);
	}

	/**
	 * @return the variableBudgetElementDTO
	 */
	public VariableBudgetElementDTO getVariableBudgetElementDTO() {
		return get("variableBudgetElementDTO");
	}

	/**
	 * @param variableBudgetElementDTO
	 *            the variableBudgetElementDTO to set
	 */
	public void setVariableBudgetElementDTO(VariableBudgetElementDTO variableBudgetElementDTO) {
		set("variableBudgetElementDTO", variableBudgetElementDTO);
	}

}
