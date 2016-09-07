package org.sigmah.client.ui.widget.form;

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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.button.ClickableLabel;
import org.sigmah.client.util.ClientUtils;

public class ListComboBox<T extends ModelData> extends Composite {
	private ListStore<T> dataStore = new ListStore<T>();
	private ListStore<T> availableValuesStore = new ListStore<T>();
	private String noAvailableValueTooltip;

	private ComboBox<T> comboBox;
	private String valueField;
	private String displayField;
	private FlexTable flexTable;
	private FlowPanel formPanel;
	private Panel rootPanel;
	private boolean enabled = true;

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

		availableValuesStore.addStoreListener(new StoreListener<T>() {
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
		});
	}

	public void initComponent() {
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

	public Field<T> getField() {
		return comboBox;
	}

	public void copyAvailableValueStore(ListStore<T> store) {
		availableValuesStore.add(store.getModels());

		store.addStoreListener(new StoreListener<T>() {
			@Override
			public void storeAdd(StoreEvent<T> se) {
				super.storeAdd(se);

				availableValuesStore.add(se.getModels());
			}

			@Override
			public void storeRemove(StoreEvent<T> se) {
				super.storeRemove(se);

				for (T model : se.getModels()) {
					availableValuesStore.remove(model);
				}
			}
		});
	}

	public void setNoAvailableValueTooltip(String noAvailableValueTooltip) {
		this.noAvailableValueTooltip = noAvailableValueTooltip;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected void buildComponent() {
		if (rootPanel == null) {
			// not yet initialized
			return;
		}

		rootPanel.clear();

		formPanel = new FlowPanel();
		formPanel.addStyleName("list-combobox__form");

		if (enabled) {
			comboBox = Forms.combobox(null, false, valueField, displayField, availableValuesStore);
			comboBox.setStyleName("list-combobox__form__choices");
			if (availableValuesStore.getModels().isEmpty() && !ClientUtils.isBlank(noAvailableValueTooltip)) {
				comboBox.setToolTip(noAvailableValueTooltip);
			}
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
		} else {
			comboBox = null;
		}

		formPanel.setWidth("100%");
		rootPanel.add(formPanel);

		FlowPanel elementsPanel = new FlowPanel();
		elementsPanel.setStyleName("list-combobox__elements");
		for (int i = 0; i < dataStore.getModels().size(); i++) {
			final T element = dataStore.getModels().get(i);
			Widget label;
			if (enabled) {
				label = new ClickableLabel(element.get(displayField).toString());
				label.addStyleName("list-combobox__elements__element");
				((ClickableLabel)label).addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						availableValuesStore.add(element);
						dataStore.remove(element);
					}
				});
			} else {
				label = new Label(i == 0 ? element.get(displayField).toString() : ", " + element.get(displayField).toString());
			}
			elementsPanel.add(label);
		}
		rootPanel.add(elementsPanel);
	}

	protected Panel getButtonPanel() {
		return formPanel;
	}
}
