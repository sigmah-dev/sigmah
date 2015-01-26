package org.sigmah.offline.js;

import org.sigmah.shared.dto.value.BudgetPartsListValueDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class BudgetPartsListValueJS extends ListableValueJS {
	
	protected BudgetPartsListValueJS() {
	}
	
	public static BudgetPartsListValueJS toJavaScript(BudgetPartsListValueDTO budgetPartsListValueDTO) {
		final BudgetPartsListValueJS budgetPartsListValueJS = Values.createJavaScriptObject(BudgetPartsListValueJS.class);
		budgetPartsListValueJS.setListableValueType(Type.BUDGET_PARTS_LIST);
		
		budgetPartsListValueJS.setId(budgetPartsListValueDTO.getId());
		
		return budgetPartsListValueJS;
	}
	
	@Override
	public BudgetPartsListValueDTO toDTO() {
		final BudgetPartsListValueDTO fileDTO = new BudgetPartsListValueDTO();
		
		fileDTO.setId(getId());
		
		return fileDTO;
	}

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
}
