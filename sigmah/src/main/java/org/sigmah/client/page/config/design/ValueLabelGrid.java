package org.sigmah.client.page.config.design;

import java.util.Arrays;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;

public class ValueLabelGrid extends EditorGrid<ModelData> {

	public ValueLabelGrid() {
		super(new ListStore<ModelData>(), createColumnModel());
		setAutoExpandColumn("label");
		setHeight(100);
		setBorders(true);
		
		getStore().add(new BaseModelData());
		
		addListener(Events.AfterEdit, new Listener<GridEvent<ModelData>>() {

			@Override
			public void handleEvent(GridEvent<ModelData> be) {
				if(be.getRowIndex()+1 == getStore().getCount() &&
						(be.getModel().get("label") != null)) {
					getStore().add(new BaseModelData());
				}
			}
		});
	}
	
	private static ColumnModel createColumnModel() {
		
		NumberField codeField = new NumberField();
		
		
		TextField<String> labelField = new TextField<String>();
		
		ColumnConfig label = new ColumnConfig("label", "Label", 150);
		label.setEditor(new CellEditor(labelField));
		label.setSortable(false);
		label.setMenuDisabled(true);
		
		return new ColumnModel(Arrays.asList(label));
		
	}
	

}
