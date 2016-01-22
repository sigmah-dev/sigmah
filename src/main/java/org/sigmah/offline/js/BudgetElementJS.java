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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class BudgetElementJS extends DefaultFlexibleElementJS {
	
	protected BudgetElementJS() {
	}
	
	public static BudgetElementJS toJavaScript(BudgetElementDTO budgetElementDTO) {
		final BudgetElementJS budgetElementJS = Values.createJavaScriptObject(BudgetElementJS.class);
		
		budgetElementJS.setBudgetSubFields(budgetElementDTO.getBudgetSubFields());
		budgetElementJS.setRatioDividend(budgetElementDTO.getRatioDividend());
		budgetElementJS.setRatioDivisor(budgetElementDTO.getRatioDivisor());
		
		return budgetElementJS;
	}

	public BudgetElementDTO toBudgetElementDTO() {
		final BudgetElementDTO budgetElementDTO = new BudgetElementDTO();
		
		budgetElementDTO.setBudgetSubFields(getBudgetSubFieldsDTO());
		
		if(getRatioDividend() != null) {
			boolean found = false;
			
			if(budgetElementDTO.getBudgetSubFields() != null) {
				final int id = getRatioDividend().getId();
				final Iterator<BudgetSubFieldDTO> iterator = budgetElementDTO.getBudgetSubFields().iterator();
				
				while(!found && iterator.hasNext()) {
					final BudgetSubFieldDTO entry = iterator.next();
					if(entry.getId().equals(id)) {
						budgetElementDTO.setRatioDividend(entry);
						found = true;
					}
				}
			}
			
			if(!found) {
				budgetElementDTO.setRatioDividend(getRatioDividend().toDTO());
			}
		}
		
		if(getRatioDivisor() != null) {
			boolean found = false;
			
			if(budgetElementDTO.getBudgetSubFields() != null) {
				final int id = getRatioDivisor().getId();
				final Iterator<BudgetSubFieldDTO> iterator = budgetElementDTO.getBudgetSubFields().iterator();
				
				while(!found && iterator.hasNext()) {
					final BudgetSubFieldDTO entry = iterator.next();
					if(entry.getId().equals(id)) {
						budgetElementDTO.setRatioDivisor(entry);
						found = true;
					}
				}
			}
			
			if(!found) {
				budgetElementDTO.setRatioDivisor(getRatioDivisor().toDTO());
			}
		}
		
		return budgetElementDTO;
	}

	public List<BudgetSubFieldDTO> getBudgetSubFieldsDTO() {
		final ArrayList<BudgetSubFieldDTO> list = new ArrayList<BudgetSubFieldDTO>();
		if(getBudgetSubFields() != null) {
			final JsArray<BudgetSubFieldJS> budgetSubFields = getBudgetSubFields();
			
			for(int index = 0; index < budgetSubFields.length(); index++) {
				list.add(budgetSubFields.get(index).toDTO());
			}
		}
		return list;
	}
	
	public native JsArray<BudgetSubFieldJS> getBudgetSubFields() /*-{
		return this.budgetSubFields;
	}-*/;

	public void setBudgetSubFields(List<BudgetSubFieldDTO> budgetSubFields) {
		if(budgetSubFields != null) {
			final JsArray<BudgetSubFieldJS> array = (JsArray<BudgetSubFieldJS>) JavaScriptObject.createArray();
			
			for(final BudgetSubFieldDTO budgetSubFieldDTO : budgetSubFields) {
				array.push(BudgetSubFieldJS.toJavaScript(budgetSubFieldDTO));
			}
			
			setBudgetSubFields(array);
		}
	}
	
	public native void setBudgetSubFields(JsArray<BudgetSubFieldJS> budgetSubFields) /*-{
		this.budgetSubFields = budgetSubFields;
	}-*/;

	public native BudgetSubFieldJS getRatioDividend() /*-{
		return this.ratioDividend;
	}-*/;

	public void setRatioDividend(BudgetSubFieldDTO ratioDividend) {
		if(ratioDividend != null) {
			setRatioDividend(BudgetSubFieldJS.toJavaScript(ratioDividend));
		}
	}
	
	public native void setRatioDividend(BudgetSubFieldJS ratioDividend) /*-{
		this.ratioDividend = ratioDividend;
	}-*/;

	public native BudgetSubFieldJS getRatioDivisor() /*-{
		return this.ratioDivisor;
	}-*/;

	public void setRatioDivisor(BudgetSubFieldDTO ratioDivisor) {
		if(ratioDivisor != null) {
			setRatioDivisor(BudgetSubFieldJS.toJavaScript(ratioDivisor));
		}
	}
	
	public native void setRatioDivisor(BudgetSubFieldJS ratioDivisor) /*-{
		this.ratioDivisor = ratioDivisor;
	}-*/;
}
