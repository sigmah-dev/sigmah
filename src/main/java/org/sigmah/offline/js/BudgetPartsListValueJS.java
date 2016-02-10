package org.sigmah.offline.js;

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
	
	public BudgetPartsListValueDTO toBudgetPartsListValueDTO() {
		final BudgetPartsListValueDTO fileDTO = new BudgetPartsListValueDTO();
		
		fileDTO.setId(getId());
		
		return fileDTO;
	}

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
}
