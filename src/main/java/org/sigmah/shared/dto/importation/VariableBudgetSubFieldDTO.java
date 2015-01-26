package org.sigmah.shared.dto.importation;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;

/**
 * VariableBudgetSubFieldDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class VariableBudgetSubFieldDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -171871913478215607L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
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
	 *          the variableDTO to set
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
	 *          the budgetSubFieldDTO to set
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
	 *          the variableBudgetElementDTO to set
	 */
	public void setVariableBudgetElementDTO(VariableBudgetElementDTO variableBudgetElementDTO) {
		set("variableBudgetElementDTO", variableBudgetElementDTO);
	}

}
