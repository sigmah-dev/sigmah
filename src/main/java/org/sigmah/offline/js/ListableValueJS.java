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
