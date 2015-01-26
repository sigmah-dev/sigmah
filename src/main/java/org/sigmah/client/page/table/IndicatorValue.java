package org.sigmah.client.page.table;

import com.extjs.gxt.ui.client.data.BaseModelData;

class IndicatorValue extends BaseModelData {

	public IndicatorValue(Double value, String label) {
		set("value", value);
		set("label", label);
	}

	public IndicatorValue(int value, String label) {
		this((double)value, label);
	}
	
	public IndicatorValue(Double dvalue) {
		set("value", dvalue);
	}

	public String getLabel() {
		return get("label");
	}
	
	public Double getValue() {
		return (Double)get("value");
	}

}
