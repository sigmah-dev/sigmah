package org.sigmah.shared.dto.importation;

import java.util.List;

import org.sigmah.shared.dto.EntityDTO;

public class VariableBudgetElementDTO extends VariableFlexibleElementDTO implements EntityDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3937014933514084026L;

	@Override
	public int getId() {
		if (get("id") != null)
			return (Integer) get("id");
		else
			return -1;
	}

	@Override
	public String getEntityName() {
		return "importation.VariableBudgetElement";
	}

	/**
	 * @return the variableSubFieldsDTO
	 */
	public List<VariableBudgetSubFieldDTO> getVariableBudgetSubFieldsDTO() {
		return get("variableBudgetSubFieldsDTO");
	}

	/**
	 * @param variableSubFieldsDTO
	 *            the variableSubFieldsDTO to set
	 */
	public void setVariableBudgetSubFieldsDTO(List<VariableBudgetSubFieldDTO> variableBudgetSubFieldsDTO) {
		set("variableBudgetSubFieldsDTO", variableBudgetSubFieldsDTO);
	}

}
