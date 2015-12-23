package org.sigmah.client.ui.widget.form;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.button.ClickableLabel;

public class ListComboBox<T extends ModelData> extends Composite {
	private ListStore<T> dataStore = new ListStore<T>();
	private ListStore<T> availableValuesStore = new ListStore<T>();

	private String valueField;
	private String displayField;
	private FlexTable flexTable;
	private Panel rootPanel;

	public ListComboBox(String valueField, String displayField) {
		super();

		this.valueField = valueField;
		this.displayField = displayField;

		dataStore.addStoreListener(new StoreListener<T>() {
			@Override
			public void storeAdd(StoreEvent<T> se) {
				super.storeAdd(se);

				ListComboBox.this.buildComponent();
			}

			@Override
			public void storeClear(StoreEvent<T> se) {
				super.storeClear(se);

				ListComboBox.this.buildComponent();
			}

			@Override
			public void storeRemove(StoreEvent<T> se) {
				super.storeRemove(se);

				ListComboBox.this.buildComponent();
			}

			@Override
			public void storeDataChanged(StoreEvent<T> se) {
				super.storeDataChanged(se);

				ListComboBox.this.buildComponent();
			}
		});

		rootPanel = new FlowPanel();

		flexTable = new FlexTable();
		flexTable.setWidth("300px");

		buildComponent();
		initWidget(rootPanel);
	}

	public ListStore<T> getListStore() {
		return dataStore;
	}

	public ListStore<T> getAvailableValuesStore() {
		return availableValuesStore;
	}

	private void buildComponent() {
		rootPanel.clear();

		final ComboBox<T> comboBox = Forms.combobox(null, false, valueField, displayField, availableValuesStore);
		comboBox.setStyleName("list-combobox__form__choices");
		if (availableValuesStore.getModels().isEmpty()) {
			comboBox.setToolTip(I18N.CONSTANTS.noAvailableProfileToAddInDefaultTeamMemberProfiles());
		}
		FlowPanel formPanel = new FlowPanel();
		formPanel.addStyleName("list-combobox__form");
		formPanel.add(comboBox);
		Button addButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent event) {
				T value = comboBox.getValue();
				if (value == null) {
					return;
				}

				if (dataStore.findModel(valueField, value.get(valueField)) != null) {
					return;
				}
				availableValuesStore.remove(value);
				dataStore.add(value);
			}
		});
		formPanel.add(addButton);
		formPanel.setWidth("100%");
		rootPanel.add(formPanel);

		FlowPanel elementsPanel = new FlowPanel();
		elementsPanel.setStyleName("list-combobox__elements");
		for (final T element : dataStore.getModels()) {
			final ClickableLabel label = new ClickableLabel(element.get(displayField).toString());
			label.addStyleName("list-combobox__elements__element");

			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					availableValuesStore.add(element);
					dataStore.remove(element);
				}
			});
			elementsPanel.add(label);
		}
		rootPanel.add(elementsPanel);
	}
}
