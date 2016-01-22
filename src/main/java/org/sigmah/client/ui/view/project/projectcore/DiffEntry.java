package org.sigmah.client.ui.view.project.projectcore;

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


import com.extjs.gxt.ui.client.data.BaseModelData;

import org.sigmah.shared.dto.element.FlexibleElementDTO;


public class DiffEntry extends BaseModelData {

	public static final String FIELD = "field";
	public static final String FIELD_NAME = "fieldName";
	public static final String VALUE_1 = "value1";
	public static final String VALUE_2 = "value2";
	public static final String DISPLAY_VALUE_1 = "displayValue1";
	public static final String DISPLAY_VALUE_2 = "displayValue2";
	
	
	public FlexibleElementDTO getField() {
		return get(FIELD);
	}

	public void setField(FlexibleElementDTO field) {
		set(FIELD, field);
		set(FIELD_NAME, field.getFormattedLabel());
	}

	public String getFieldName() {
		return get(FIELD_NAME);
	}
	
	public void setValue(int index, String value) {
		if(index == 0) {
			setValue1(value);
		} else {
			setValue2(value);
		}
	}
	
	public Object getValue1() {
		return get(VALUE_1);
	}

	public void setValue1(String value1) {
		set(VALUE_1, value1);
		set(DISPLAY_VALUE_1, getField().toHTML(value1));
	}
	
	public Object getDisplayValue1() {
		return get(DISPLAY_VALUE_1);
	}

	public Object getValue2() {
		return get(VALUE_2);
	}

	public void setValue2(String value2) {
		set(VALUE_2, value2);
		set(DISPLAY_VALUE_2, getField().toHTML(value2));
	}
	
	public Object getDisplayValue2() {
		return get(DISPLAY_VALUE_2);
	}

}
