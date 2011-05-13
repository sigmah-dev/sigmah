package org.sigmah.client.page.table;

import com.extjs.gxt.ui.client.data.BaseModelData;

class IndicatorValue extends BaseModelData {

	public IndicatorValue(int value, String label) {
		set("value", (double)value);
		set("label", label);
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
