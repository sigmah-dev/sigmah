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

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;

public class IndicatorValueEditor extends CellEditor {

	public IndicatorValueEditor() {
		super(new IndicatorValueField());
	}


	@Override
	public Object preProcessValue(Object value) {
		if(value == null) {
			return null;
		}
		ListStore<IndicatorValue> store = ((IndicatorValueField)getField()).getStore();
		int code = ((Double)value).intValue();
		if(code >= 1 && code <= store.getCount()) {
			return store.getAt(code-1);
		}
		
		return new IndicatorValue((Double)value);
	}
	

	@Override
	public Object postProcessValue(Object value) {
		if(value == null) {
			return null;
		} else {
			return ((IndicatorValue)value).getValue();
		}
		
	}	

}
