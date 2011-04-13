package org.sigmah.client.page.table;

import java.util.List;

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
