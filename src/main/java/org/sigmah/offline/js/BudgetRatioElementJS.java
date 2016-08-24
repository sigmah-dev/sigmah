package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.BudgetRatioElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * JavaScript version of {@link BudgetRatioElementDTO}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class BudgetRatioElementJS extends DefaultFlexibleElementJS {
	
	/**
	 * Creates a JavaScript version of the given element.
	 * 
	 * @param budgetRatioElementDTO
	 *			DTO to convert to JavaScript.
	 * @return A JavaScript version of the given element.
	 */
	public static BudgetRatioElementJS toJavaScript(final BudgetRatioElementDTO budgetRatioElementDTO) {
		final BudgetRatioElementJS budgetRatioElementJS = Values.createJavaScriptObject();
		
		budgetRatioElementJS.setPlannedBudget(budgetRatioElementDTO.getPlannedBudget());
		budgetRatioElementJS.setSpentBudget(budgetRatioElementDTO.getSpentBudget());
		
		return budgetRatioElementJS;
	}
	
	/**
	 * Creates a DTO with the values of this JavaScript object.
	 * 
	 * @return A new <code>BudgetRatioElementDTO</code>.
	 */
	public BudgetRatioElementDTO toBudgetRatioElementDTO() {
		final BudgetRatioElementDTO budgetRatioElementDTO = new BudgetRatioElementDTO();
		
		budgetRatioElementDTO.setPlannedBudget(getPlannedBudget());
		budgetRatioElementDTO.setSpentBudget(getSpentBudget());
		
		return budgetRatioElementDTO;
	}
	
	/**
	 * Protected constructor, required for JavaScript objects.
	 */
	protected BudgetRatioElementJS() {
		// Empty.
	}
	
	public FlexibleElementDTO getSpentBudget() {
		final FlexibleElementJS elementJS = Values.getJavaScriptObject(this, BudgetRatioElementDTO.SPENT_BUDGET);
		if (elementJS != null) {
			return elementJS.toDTO();
		} else {
			return null;
		}
	}
	
	public void setSpentBudget(final FlexibleElementDTO spentBudget) {
		if (spentBudget == null) {
			return;
		}
		
		final FlexibleElementJS elementJS = FlexibleElementJS.toJavaScript(spentBudget);
		Values.setJavaScriptObject(this, BudgetRatioElementDTO.SPENT_BUDGET, elementJS);
	}
	
	public FlexibleElementDTO getPlannedBudget() {
		final FlexibleElementJS elementJS = Values.getJavaScriptObject(this, BudgetRatioElementDTO.PLANNED_BUDGET);
		if (elementJS != null) {
			return elementJS.toDTO();
		} else {
			return null;
		}
	}

	public void setPlannedBudget(final FlexibleElementDTO plannedBudget) {
		if (plannedBudget == null) {
			return;
		}
		
		final FlexibleElementJS elementJS = FlexibleElementJS.toJavaScript(plannedBudget);
		Values.setJavaScriptObject(this, BudgetRatioElementDTO.PLANNED_BUDGET, elementJS);
	}

}
