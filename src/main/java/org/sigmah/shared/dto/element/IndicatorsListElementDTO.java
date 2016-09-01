package org.sigmah.shared.dto.element;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.value.IndicatorsListValueDTO;
import org.sigmah.shared.dto.value.ListableValue;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * IndicatorsListElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IndicatorsListElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
	
	/**
	 * Entity name mapped by the current DTO starting from the "server.domain" package name.
	 */
	public static final String ENTITY_NAME = "element.IndicatorsListElement";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {

		if (result == null || result.getValuesObject() == null) {
			return false;
		}

		return !result.getValuesObject().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {

		// Creates actions menu to manage the files list.
		final Button addButton = new Button(I18N.CONSTANTS.flexibleElementIndicatorsListAdd());

		final Button removeButton = new Button(I18N.CONSTANTS.remove());
		removeButton.setEnabled(false);

		final ToolBar actionsToolBar = new ToolBar();
		actionsToolBar.add(addButton);
		actionsToolBar.add(removeButton);

		// Fills the grid store with the files list.
		final ListStore<IndicatorDTO> store = new ListStore<IndicatorDTO>();

		if (valueResult != null && valueResult.isValueDefined()) {
			for (ListableValue s : valueResult.getValuesObject()) {
				store.add(((IndicatorsListValueDTO) s).getIndicatorDTO());
			}
		}

		// Grid plugins.
		final CheckBoxSelectionModel<IndicatorDTO> selectionModel = new CheckBoxSelectionModel<IndicatorDTO>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);

		// Creates the grid which contains the files list.

		final EditorGrid<IndicatorDTO> grid = new EditorGrid<IndicatorDTO>(store, getColumnModel(selectionModel));
		grid.setSelectionModel(selectionModel);
		grid.setAutoExpandColumn("name");
		grid.setBorders(false);
		grid.getView().setForceFit(true);
		grid.addPlugin(selectionModel);

		// Creates the main panel.
		final ContentPanel panel = new ContentPanel();
		panel.setHeadingText(getLabel());
		panel.setLayout(new FitLayout());

		panel.setTopComponent(actionsToolBar);
		panel.add(grid);

		// Detects additions and deletions in the store and adjusts the grid
		// height accordingly.
		grid.addListener(Events.ViewReady, new Listener<ComponentEvent>() {

			@Override
			public void handleEvent(ComponentEvent be) {
				grid.getStore().addListener(Store.Add, new Listener<StoreEvent<IndicatorDTO>>() {

					@Override
					public void handleEvent(StoreEvent<IndicatorDTO> be) {
						doAutoHeight(grid, panel);
					}
				});
				grid.getStore().addListener(Store.Remove, new Listener<StoreEvent<IndicatorDTO>>() {

					@Override
					public void handleEvent(StoreEvent<IndicatorDTO> be) {
						doAutoHeight(grid, panel);
					}
				});
				doAutoHeight(grid, panel);
			}
		});

		// Manages action buttons activations.
		selectionModel.addSelectionChangedListener(new SelectionChangedListener<IndicatorDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<IndicatorDTO> se) {
				final List<IndicatorDTO> selection = se.getSelection();
				final boolean enabledState = selection != null && !selection.isEmpty();
				removeButton.setEnabled(enabledState);
			}
		});

		// Buttons listeners.
		addButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				// TODO implements
				N10N.info("Unsupported operation", "Method not yet implemented.");
			}
		});

		removeButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {

				final IndicatorDTO indicator = selectionModel.getSelectedItem();

				// Asks the client to confirm the indicator removal.
				N10N.confirmation(I18N.CONSTANTS.flexibleElementIndicatorsListRemoval(), I18N.MESSAGES.flexibleElementIndicatorsListConfirmRemove(indicator.getName()),
					new ConfirmCallback() {

						@Override
						public void onAction() {
							// Removes it.
							// TODO implements
							N10N.info("Unsupported operation", "Method not yet implemented.");
						}
					});
			}
		});

		panel.setEnabled(enabled);

		return panel;
	}

	/**
	 * Defines the column model for the files list grid.
	 * 
	 * @param selectionModel
	 *          The grid selection model.
	 * @return The column model.
	 */
	private ColumnModel getColumnModel(CheckBoxSelectionModel<IndicatorDTO> selectionModel) {

		final List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

		columnConfigs.add(selectionModel.getColumn());

		// Incator's code.
		ColumnConfig column = new ColumnConfig();
		column.setId("code");
		column.setHeaderText(I18N.CONSTANTS.flexibleElementIndicatorsListCode());
		column.setEditor(null);
		column.setWidth(30);
		columnConfigs.add(column);

		// Indicator's name.
		column = new ColumnConfig();
		column.setId("name");
		column.setHeaderText(I18N.CONSTANTS.flexibleElementIndicatorsListName());
		column.setEditor(null);
		column.setWidth(100);
		columnConfigs.add(column);

		// Indicator's unit.
		column = new ColumnConfig();
		column.setId("units");
		column.setHeaderText(I18N.CONSTANTS.flexibleElementIndicatorsListUnits());
		column.setEditor(null);
		column.setWidth(80);
		columnConfigs.add(column);

		return new ColumnModel(columnConfigs);
	}

	/**
	 * Adjusts the grid height for the current elements number.
	 * 
	 * @param grid
	 *          The grid.
	 * @param cp
	 *          The grid's parent panel.
	 */
	private void doAutoHeight(Grid<IndicatorDTO> grid, ContentPanel cp) {
		if (grid.isViewReady()) {
			cp.setHeight((grid.getView().getBody().isScrollableX() ? 20 : 0)
				+ grid.el().getFrameWidth("tb")
				+ grid.getView().getHeader().getHeight()
				+ cp.getFrameHeight()
				+ grid.getView().getBody().firstChild().getHeight());
		}
	}

}
