package org.sigmah.client.ui.widget;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SimpleComboBoxData extends BaseModelData {

	private static final long serialVersionUID = 3241619100800404244L;

	public SimpleComboBoxData(Integer value, String label) {
		setValue(value);
		setLabel(label);
	}

	public Integer getValue() {
		return get("value");
	}

	public void setValue(Integer value) {
		set("value", value);
	}

	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}
}
