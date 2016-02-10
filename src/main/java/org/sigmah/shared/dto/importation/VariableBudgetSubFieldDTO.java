package org.sigmah.shared.dto.importation;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
