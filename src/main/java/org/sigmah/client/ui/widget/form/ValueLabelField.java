package org.sigmah.client.ui.widget.form;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import java.util.Collections;
import org.sigmah.client.i18n.I18N;

/**
 * Form field element to specify the possible values of a qualitative indicator.
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2
 */
public class ValueLabelField extends AdapterField {

	private static final String LABEL_PROPERTY = "label";

	private final EditorGrid<ValueLabel> grid;
	private final ListStore<ValueLabel> store;

	public ValueLabelField() {
		super(createGrid());
		grid = (EditorGrid<ValueLabel>) this.getWidget();
		store = grid.getStore();
		grid.addListener(Events.AfterEdit, new Listener<GridEvent>() {
			@Override
			public void handleEvent(GridEvent be) {
				Object oldValue = value;
				value = valueFromStore();
				fireChangeEvent(oldValue, value);
			}
		});
	}

	protected List<String> valueFromStore() {
		List<String> list = new ArrayList<String>();
		for(ValueLabel model : store.getModels()) {
			if(model.hasLabel()) {
				list.add(model.getLabel());
			}
		}
		return list;
	}

	private static EditorGrid<ValueLabel> createGrid() {
		final EditorGrid<ValueLabel> grid = new EditorGrid<ValueLabel>(new ListStore<ValueLabel>(), createColumnModel());
		grid.setAutoExpandColumn(LABEL_PROPERTY);
		grid.setHeight(100);
		grid.setBorders(true);

		grid.getStore().add(new ValueLabel());

		grid.addListener(Events.AfterEdit, new Listener<GridEvent<ValueLabel>>() {

			@Override
			public void handleEvent(GridEvent<ValueLabel> be) {
				if(be.getRowIndex()+1 == grid.getStore().getCount() &&
						(be.getModel().getLabel() != null)) {
					grid.getStore().add(new ValueLabel());
				}
			}
		});
		return grid;
	}

	private static ColumnModel createColumnModel() {
		TextField<String> labelField = new TextField<String>();

		ColumnConfig label = new ColumnConfig(LABEL_PROPERTY, I18N.CONSTANTS.indicatorLabel(), 150);
		label.setEditor(new CellEditor(labelField));
		label.setSortable(false);
		label.setMenuDisabled(true);

		return new ColumnModel(Collections.singletonList(label));
	}

	@Override
	public void setValue(Object value) {
		List<String> list = (List<String>) value;
		store.removeAll();
		if(list != null) {
			for(String label: list) {
				store.add(new ValueLabel(label));
			}
		}
		store.add(new ValueLabel());	
	}
	
	@Override
	public List<String> getValue() {
		return (List<String>) value;
	}


	private static class ValueLabel extends BaseModelData {

		public ValueLabel() {

		}

		public ValueLabel(String label) {
			set(LABEL_PROPERTY, label);
		}

		public boolean hasLabel() {
			return getLabel() != null;
		}

		public String getLabel() {
			return get(LABEL_PROPERTY);
		}

	}
}
