package org.sigmah.shared.dto.importation;

import java.util.List;

/**
 * VariableBudgetElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class VariableBudgetElementDTO extends VariableFlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3937014933514084026L;

	/**
	 * {@inheritDoc}
	 */
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
	 * @param variableBudgetSubFieldsDTO
	 *          the VariableBudgetSubFieldDTO to set
	 */
	public void setVariableBudgetSubFieldsDTO(List<VariableBudgetSubFieldDTO> variableBudgetSubFieldsDTO) {
		set("variableBudgetSubFieldsDTO", variableBudgetSubFieldsDTO);
	}

}
