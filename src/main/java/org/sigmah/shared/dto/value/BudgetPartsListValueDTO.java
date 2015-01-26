package org.sigmah.shared.dto.value;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * BudgetPartsListValueDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BudgetPartsListValueDTO extends AbstractModelDataEntityDTO<Integer> implements ListableValue {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.BudgetPartsListValue";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

}
