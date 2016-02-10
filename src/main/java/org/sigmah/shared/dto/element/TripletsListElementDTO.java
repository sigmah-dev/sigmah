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


import java.util.Arrays;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import java.util.ArrayList;

/**
 * TripletsListElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TripletsListElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		// Gets the entity name mapped by the current DTO starting from the "server.domain" package name.
		return "element.TripletsListElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {

		if (result == null || !result.isValueDefined()) {
			return false;
		}

		return !result.getValuesObject().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, final boolean enabled) {
		final boolean canEdit = enabled && userCanPerformChangeType(ValueEventChangeType.EDIT);
    	final boolean canDelete = enabled && userCanPerformChangeType(ValueEventChangeType.REMOVE);
    	final boolean canAdd = enabled && userCanPerformChangeType(ValueEventChangeType.ADD);

		// Creates actions toolbar to manage the triplets list.
		final Button addButton = new Button(I18N.CONSTANTS.addItem());
		addButton.setEnabled(canAdd);

		final Button deleteButton = new Button(I18N.CONSTANTS.remove());
		deleteButton.setEnabled(false);

		final ToolBar actionsToolBar = new ToolBar();
		actionsToolBar.add(addButton);
		if (canDelete) {
			actionsToolBar.add(new SeparatorToolItem());
			actionsToolBar.add(deleteButton);
		}

		// Creates the top panel of the grid.
		final ContentPanel topPanel = new ContentPanel();
		topPanel.setHeaderVisible(false);
		topPanel.setLayout(new FlowLayout());

		topPanel.add(actionsToolBar);

		// Fills the grid store with the triplets lists
		final ListStore<TripletValueDTO> store = new ListStore<TripletValueDTO>();

		if (valueResult != null && valueResult.isValueDefined()) {
			for (ListableValue s : valueResult.getValuesObject()) {
				store.add((TripletValueDTO) s);
			}
		}
		
		// Selecting the selection model based on user rights.
		final GridSelectionModel<TripletValueDTO> selectionModel = canDelete ?
			new CheckBoxSelectionModel<TripletValueDTO>() :
			new GridSelectionModel<TripletValueDTO>();

		// Creates the grid which contains the triplets list.
		final FlexibleGrid<TripletValueDTO> grid = new FlexibleGrid<TripletValueDTO>(store, selectionModel, 
			getColumnModel(selectionModel, canEdit));
		grid.setAutoExpandColumn("name");
		grid.setVisibleElementsCount(5);

		// Creates the main panel.
		final ContentPanel panel = new ContentPanel();
		panel.setHeadingText(getLabel());
		panel.setBorders(true);
		panel.setLayout(new FitLayout());
		panel.add(grid);
		
		if (canAdd || canDelete) {
        	panel.setTopComponent(topPanel);
        }

		grid.addListener(Events.AfterEdit, new Listener<GridEvent<TripletValueDTO>>() {

			@Override
			public void handleEvent(GridEvent<TripletValueDTO> be) {
				// Edit an existing triplet
				final TripletValueDTO valueDTO = grid.getStore().getAt(be.getRowIndex());

				valueDTO.setIndex(be.getRowIndex());

				handlerManager.fireEvent(new ValueEvent(TripletsListElementDTO.this, valueDTO, ValueEventChangeType.EDIT));
			}

		});

		// Manages action buttons activations.
		selectionModel.addSelectionChangedListener(new SelectionChangedListener<TripletValueDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<TripletValueDTO> se) {
				final List<TripletValueDTO> selection = se.getSelection();

				final boolean enabledState = selection != null && !selection.isEmpty();
				deleteButton.setEnabled(enabledState);
			}
		});

		// Buttons listeners.
		addButton.addListener(Events.OnClick, new ActionListener(grid, true));
		deleteButton.addListener(Events.OnClick, new ActionListener(grid, false));

		return panel;
	}

	/**
	 * Defines the column model for the triplets list grid.
	 * 
	 * @param selectionModel
	 *          The grid selection model.
	 * @param canEdit 
	 *          editing right of the current user.
	 * @return The column model.
	 */
	private ColumnConfig[] getColumnModel(GridSelectionModel<TripletValueDTO> selectionModel, boolean canEdit) {

		final ColumnConfig codeColumn = new ColumnConfig();
		codeColumn.setId("code");
		codeColumn.setHeaderText(I18N.CONSTANTS.flexibleElementTripletsListCode());
		codeColumn.setWidth(100);

		final ColumnConfig nameColumn = new ColumnConfig();
		nameColumn.setId("name");
		nameColumn.setHeaderText(I18N.CONSTANTS.flexibleElementTripletsListName());
		nameColumn.setWidth(100);

		final ColumnConfig periodColumn = new ColumnConfig();
		periodColumn.setId("period");
		periodColumn.setHeaderText(I18N.CONSTANTS.flexibleElementTripletsListPeriod());
		periodColumn.setWidth(60);
		
		if (canEdit) {
			// Adds editors to all column configs.
			for(final ColumnConfig config : Arrays.asList(codeColumn, nameColumn, periodColumn)) {
				final TextField<String> text = new TextField<String>();
				text.setAllowBlank(false);
				config.setEditor(new CellEditor(text));
			}
		}

		if(selectionModel instanceof CheckBoxSelectionModel) {
			return new ColumnConfig[] {
				((CheckBoxSelectionModel<TripletValueDTO>) selectionModel).getColumn(),
				codeColumn,
				nameColumn,
				periodColumn
			};
			
		} else {
			return new ColumnConfig[] {
				codeColumn,
				nameColumn,
				periodColumn
			};
		}
	}

	/**
	 * Manages grid actions.
	 * 
	 * @author tmi
	 */
	private class ActionListener implements Listener<ButtonEvent> {

		private EditorGrid<TripletValueDTO> grid;
		private boolean isAddAction;

		public ActionListener(EditorGrid<TripletValueDTO> grid, boolean isAddAction) {
			this.grid = grid;
			this.isAddAction = isAddAction;
		}

		@Override
		public void handleEvent(ButtonEvent be) {

			// Add a new triplet
			if (isAddAction) {
				TripletValueDTO addedValue = new TripletValueDTO();
				addedValue.setCode("-" + I18N.CONSTANTS.flexibleElementTripletsListCode() + "-");
				addedValue.setName("-" + I18N.CONSTANTS.flexibleElementTripletsListName() + "-");
				addedValue.setPeriod("-" + I18N.CONSTANTS.flexibleElementTripletsListPeriod() + "-");

				grid.getStore().add(addedValue);
				addedValue.setIndex(grid.getStore().indexOf(addedValue));

				// Fires the value change event.
				handlerManager.fireEvent(new ValueEvent(TripletsListElementDTO.this, addedValue, ValueEventChangeType.ADD));

			}
			// Remove some existing triplets
			else {
				final List<TripletValueDTO> selectedItems = new ArrayList<TripletValueDTO>(grid.getSelectionModel().getSelectedItems());
				for (TripletValueDTO removedValue : selectedItems) {
                    removedValue.setIndex(grid.getStore().indexOf(removedValue));
                    
                    // Fires the value change event.
                    handlerManager.fireEvent(new ValueEvent(TripletsListElementDTO.this, removedValue, ValueEventChangeType.REMOVE));
				}

				for (TripletValueDTO removedValue : selectedItems) {
                    grid.getStore().remove(removedValue);
                }
			}

			// Required element ?
			if (getValidates()) {
				handlerManager.fireEvent(new RequiredValueEvent(grid.getStore().getCount() > 0));
			}
		}
	}

	@Override
	public Object renderHistoryToken(final HistoryTokenListDTO token) {

		final Label details = new Label();
		details.addStyleName("history-details");

		if (token.getTokens().size() == 1) {
			details.setText("1 " + I18N.CONSTANTS.historyModification());
		} else {
			details.setText(token.getTokens().size() + " " + I18N.CONSTANTS.historyModifications());
		}

		details.addClickHandler(new ClickHandler() {

			private Window window;

			@Override
			public void onClick(ClickEvent e) {

				if (window == null) {

					// Builds window.
					window = new Window();
					window.setAutoHide(true);
					window.setModal(false);
					window.setPlain(true);
					window.setHeaderVisible(true);
					window.setClosable(true);
					window.setLayout(new FitLayout());
					window.setWidth(750);
					window.setHeight(400);
					window.setBorders(false);
					window.setResizable(true);

					// Builds grid.
					final ListStore<TripletValueDTO> store = new ListStore<TripletValueDTO>();
					final com.extjs.gxt.ui.client.widget.grid.Grid<TripletValueDTO> grid =
							new com.extjs.gxt.ui.client.widget.grid.Grid<TripletValueDTO>(store, new ColumnModel(Arrays.asList(getColumnModel())));
					grid.getView().setForceFit(true);
					grid.setBorders(false);
					grid.setAutoExpandColumn("name");

					window.add(grid);

					// Fills store.
					for (final HistoryTokenDTO t : token.getTokens()) {

						final List<String> l = ValueResultUtils.splitElements(t.getValue());

						final TripletValueDTO triplet = new TripletValueDTO();
						triplet.setCode(l.get(0));
						triplet.setName(l.get(1));
						triplet.setPeriod(l.get(2));
						triplet.setType(t.getType());

						store.add(triplet);
					}
				}

				window.setPagePosition(e.getNativeEvent().getClientX(), e.getNativeEvent().getClientY());
				window.show();
			}

			private ColumnConfig[] getColumnModel() {

				// Change type.
				final ColumnConfig typeColumn = new ColumnConfig();
				typeColumn.setId("type");
				typeColumn.setHeaderText(I18N.CONSTANTS.historyModificationType());
				typeColumn.setWidth(150);
				typeColumn.setSortable(false);
				typeColumn.setRenderer(new GridCellRenderer<TripletValueDTO>() {

					@Override
					public Object render(TripletValueDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<TripletValueDTO> store,
							Grid<TripletValueDTO> grid) {

						if (model.getType() != null) {
							switch (model.getType()) {
								case ADD:
									return I18N.CONSTANTS.historyAdd();
								case EDIT:
									return I18N.CONSTANTS.historyEdit();
								case REMOVE:
									return I18N.CONSTANTS.historyRemove();
								default:
									return "-";
							}
						} else {
							return "-";
						}
					}
				});

				// Code.
				final ColumnConfig codeColumn = new ColumnConfig();
				codeColumn.setId("code");
				codeColumn.setHeaderText(I18N.CONSTANTS.flexibleElementTripletsListCode());
				codeColumn.setWidth(200);

				// Name.
				final ColumnConfig nameColumn = new ColumnConfig();
				nameColumn.setId("name");
				nameColumn.setHeaderText(I18N.CONSTANTS.flexibleElementTripletsListName());
				nameColumn.setWidth(200);

				// Period.
				final ColumnConfig periodColumn = new ColumnConfig();
				periodColumn.setId("period");
				periodColumn.setHeaderText(I18N.CONSTANTS.flexibleElementTripletsListPeriod());
				periodColumn.setWidth(200);

				return new ColumnConfig[] {
																		typeColumn,
																		codeColumn,
																		nameColumn,
																		periodColumn
				};
			}
		});

		return details;
	}
}
