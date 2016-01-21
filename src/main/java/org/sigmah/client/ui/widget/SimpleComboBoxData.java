package org.sigmah.client.ui.widget;

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
