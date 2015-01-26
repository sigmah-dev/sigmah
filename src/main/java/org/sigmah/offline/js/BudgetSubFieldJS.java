package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class BudgetSubFieldJS extends JavaScriptObject {
	
	protected BudgetSubFieldJS() {
	}
	
	public static BudgetSubFieldJS toJavaScript(BudgetSubFieldDTO budgetSubFieldDTO) {
		final BudgetSubFieldJS budgetSubFieldJS = Values.createJavaScriptObject(BudgetSubFieldJS.class);
		
		budgetSubFieldJS.setId(budgetSubFieldDTO.getId());
		budgetSubFieldJS.setLabel(budgetSubFieldDTO.getLabel());
		budgetSubFieldJS.setBudgetElement(budgetSubFieldDTO.getBudgetElement());
		budgetSubFieldJS.setFieldOrder(budgetSubFieldDTO.getFieldOrder());
		budgetSubFieldJS.setType(budgetSubFieldDTO.getType());
		
		return budgetSubFieldJS;
	}
	
	public BudgetSubFieldDTO toDTO() {
		final BudgetSubFieldDTO budgetSubFieldDTO = new BudgetSubFieldDTO();
		
		budgetSubFieldDTO.setId(getId());
		budgetSubFieldDTO.setLabel(getLabel());
		budgetSubFieldDTO.setFieldOrder(getFieldOrderInteger());
		budgetSubFieldDTO.setType(getTypeEnum());
		
		return budgetSubFieldDTO;
	}
	
	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public native int getBudgetElement() /*-{
		return this.budgetElement;
	}-*/;

	public void setBudgetElement(BudgetElementDTO budgetElement) {
		if(budgetElement != null) {
			setBudgetElement(budgetElement.getId());
		}
	}
	
	public native void setBudgetElement(int budgetElement) /*-{
		this.budgetElement = budgetElement;
	}-*/;

	public native boolean hasFieldOrder() /*-{
		return typeof this.fieldOrder != 'undefined';
	}-*/;
	
	public Integer getFieldOrderInteger() {
		if(hasFieldOrder()) {
			return getFieldOrder();
		}
		return null;
	}
	
	public native int getFieldOrder() /*-{
		return this.fieldOrder;
	}-*/;

	public void setFieldOrder(Integer fieldOrder) {
		if(fieldOrder != null) {
			setFieldOrder(fieldOrder.intValue());
		}
	}
	
	public native void setFieldOrder(int fieldOrder) /*-{
		this.fieldOrder = fieldOrder;
	}-*/;

	public BudgetSubFieldType getTypeEnum() {
		if(getType() != null) {
			return BudgetSubFieldType.valueOf(getType());
		}
		return null;
	}
	
	public native String getType() /*-{
		return this.type;
	}-*/;

	public void setType(BudgetSubFieldType type) {
		if(type != null) {
			setType(type.name());
		}
	}
	
	public native void setType(String type) /*-{
		this.type = type;
	}-*/;
}
