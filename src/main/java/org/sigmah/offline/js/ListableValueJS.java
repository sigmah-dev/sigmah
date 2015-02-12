package org.sigmah.offline.js;

import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.BudgetPartsListValueDTO;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ListableValueJS extends JavaScriptObject {
	public static enum Type {
		BUDGET_PARTS_LIST,
		FILE,
		INDICATORS_LIST,
		REPORT_REFERENCE,
		TRIPLET;
	}
	
	protected ListableValueJS() {
	}
	
	public static ListableValueJS toJavaScript(ListableValue listableValue) {
		if(listableValue instanceof BudgetPartsListValueDTO) {
			return BudgetPartsListValueJS.toJavaScript((BudgetPartsListValueDTO)listableValue);
			
		} else if(listableValue instanceof FileDTO) {
			return FileJS.toJavaScript((FileDTO)listableValue);
			
		} else if(listableValue instanceof TripletValueDTO) {
			return TripletValueJS.toJavaScript((TripletValueDTO)listableValue);
			
		} else if(listableValue instanceof ReportReference) {
			return ReportReferenceJS.toJavaScript((ReportReference)listableValue);
		}
		// TODO: ReportReference
		throw new UnsupportedOperationException("Listable value type not supported: " + listableValue.getClass());
	}
	
	public final ListableValue toDTO() {
		switch(getListableValueTypeEnum()) {
			case BUDGET_PARTS_LIST:
				return ((BudgetPartsListValueJS)this).toBudgetPartsListValueDTO();
			case FILE:
				return ((FileJS)this).toFileDTO();
			case REPORT_REFERENCE:
				return ((ReportReferenceJS)this).toReportReference();
			case TRIPLET:
				return ((TripletValueJS)this).toTripletValueDTO();
			default:
				throw new UnsupportedOperationException("Listable value type not supported: " + getListableValueType());
		}
	}
	
	public final native String getListableValueType() /*-{
		return this.listableValueType;
	}-*/;
	
	public final Type getListableValueTypeEnum() {
		if(getListableValueType() != null) {
			return Type.valueOf(getListableValueType());
		}
		return null;
	}
	
	public final native void setListableValueType(String listableValueType) /*-{
		this.listableValueType = listableValueType;
	}-*/;
	
	public final void setListableValueType(Type type) {
		if(type != null) {
			setListableValueType(type.name());
		}
	}
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
}
