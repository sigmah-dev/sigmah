package org.sigmah.client.ui.view.project.projectcore;

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
