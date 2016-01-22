package org.sigmah.client.ui.view.pivot.table;

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
